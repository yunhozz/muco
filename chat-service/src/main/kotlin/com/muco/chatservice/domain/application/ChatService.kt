package com.muco.chatservice.domain.application

import com.muco.chatservice.domain.persistence.entity.Chat
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

    private var participants: List<RSocketRequester> = mutableListOf()

    fun onConnect(requester: RSocketRequester): Mono<Void> =
        requester.rsocket()?.let {
            it.onClose()
                .doFirst {
                    participants += requester
                }
                .doOnError { e ->
                    throw RuntimeException(e.localizedMessage)
                }
                .doFinally {
                    participants -= requester
                }
        } ?: Mono.error(RuntimeException("소켓 연결 실패"))

    fun sendMessage(senderId: Long, chatroomId: Long, dto: ChatRequestDTO): Mono<ChatRequestDTO> =
        Flux.fromIterable(participants)
            .flatMap { requester ->
                requester.route("")
                    .data(dto)
                    .send()
            }
            .flatMap {
                chatroomRepository.findById(chatroomId)
                    .flatMapMany { chatroom ->
                        chatroomUserRepository.findAllByChatroomId(chatroom.id)
                            .collectList()
                            .flatMapMany { chatroomUsers ->
                                val chatList: MutableList<Chat> = mutableListOf()
                                for (cu in chatroomUsers) {
                                    // TODO : 대화 상대의 닉네임, 프로필 사진 조회
                                    val chat = Chat(
                                        chatroomId = chatroom.id!!,
                                        senderId = cu.userId,
                                        receiverId = cu.userId,
                                        rNickname = "",
                                        rImageUrl = "",
                                        content = dto.content
                                    )
                                    chatList += chat
                                }
                                chatRepository.saveAll(chatList)
                            }
                    }
            }
            .then(Mono.defer { Mono.just(dto) })

    fun findAllChatListByChatroom(chatroomId: Long): Flux<Chat> =
        chatRepository.findAllByChatroomIdOrderByCreatedAtDesc(chatroomId)
}