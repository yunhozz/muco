package com.muco.chatservice.domain.application

import com.muco.chatservice.domain.persistence.entity.Chat
import com.muco.chatservice.domain.persistence.repository.ChatRepository
import com.muco.chatservice.global.dto.request.ChatRequestDTO
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChatService(
    private val chatRepository: ChatRepository
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

    fun message(dto: ChatRequestDTO): Mono<ChatRequestDTO> =
        this.sendMessage(dto)
            .then(Mono.defer { Mono.just(dto) })

    fun sendMessage(dto: ChatRequestDTO): Mono<Void> =
        Flux.fromIterable(participants)
            .flatMap { requester ->
                requester.route("")
                    .data(dto)
                    .send()
            }
            .then()

    fun findAllChatListByChatroom(chatroomId: Long): Flux<Chat> =
        chatRepository.findAllByChatroomIdOrderByCreatedAtDesc(chatroomId)
}