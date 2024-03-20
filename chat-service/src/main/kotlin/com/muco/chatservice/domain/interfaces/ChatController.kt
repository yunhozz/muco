package com.muco.chatservice.domain.interfaces

import com.muco.chatservice.domain.application.ChatService
import com.muco.chatservice.global.dto.request.ChatRequestDTO
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Mono

@Controller
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {

    @ConnectMapping
    fun connect(requester: RSocketRequester): Mono<Void> = chatService.onConnect(requester)

    @MessageMapping("/msg")
    fun message(@Valid @RequestBody dto: ChatRequestDTO): Mono<ChatRequestDTO> = chatService.message(dto)

    @MessageMapping("/send")
    fun send(@RequestHeader sub: String, @Valid @RequestBody dto: ChatRequestDTO): Mono<Void> = chatService.sendMessage(dto)
}