package com.muco.chatservice.domain.application

import com.muco.chatservice.domain.persistence.entity.Chat
import com.muco.chatservice.domain.persistence.entity.ChatroomUser
import com.muco.chatservice.domain.persistence.repository.ChatRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomUserRepository
import com.muco.chatservice.global.dto.request.ChatRequestDTO
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatroomRepository: ChatroomRepository,
    private val chatroomUserRepository: ChatroomUserRepository
) {

    private var participants = mutableListOf<RSocketRequester>()

    fun onConnect(requester: RSocketRequester): Mono<Void> =
        requester.rsocket()?.let {
            it.onClose()
                .doFirst {
                    participants.add(requester)
                }
                .doOnError { e ->
                    throw RuntimeException(e.localizedMessage)
                }
                .doFinally {
                    participants.remove(requester)
                }
        } ?: Mono.error(RuntimeException("소켓 연결 실패"))

    fun sendMessage(senderId: Long, chatroomId: Long, dto: ChatRequestDTO): Mono<ChatRequestDTO> =
        Flux.fromIterable(participants)
            .flatMap { requester ->
                requester.route("")
                    .data(dto)
                    .send()
            }
            .then(chatroomRepository.findById(chatroomId))
            .switchIfEmpty(Mono.error(RuntimeException("해당 채팅방을 찾을 수 없습니다. id = $chatroomId")))
            .flatMap { chatroom ->
                chatroomUserRepository.findAllByChatroomId(chatroom.id)
                    .collectList()
                    .flatMap { chatroomUsers ->
                        // TODO : 대화 상대의 닉네임, 프로필 사진 조회
                        val (sId, rId) = determineSenderIdAndReceiverId(chatroomUsers, senderId)
                        chatRepository.save(Chat(
                            chatroomId = chatroom.id!!,
                            senderId = sId,
                            receiverId = rId,
                            rNickname = "",
                            rImageUrl = "",
                            content = dto.content
                        ))
                    }
            }
            .then(Mono.just(dto))

    fun findAllChatListByChatroom(chatroomId: Long): Flux<Chat> =
        chatRepository.findAllByChatroomIdOrderByCreatedAtDesc(chatroomId)

    private fun determineSenderIdAndReceiverId(users: List<ChatroomUser>, senderId: Long): Pair<Long, Long> {
        val (user1, user2) = users
            .map(ChatroomUser::userId)
            .sorted()

        return if (user1 == senderId) Pair(user1, user2)
        else Pair(user2, user1)
    }
}