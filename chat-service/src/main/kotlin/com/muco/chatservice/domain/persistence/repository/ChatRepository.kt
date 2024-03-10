package com.muco.chatservice.domain.persistence.repository

import com.muco.chatservice.domain.persistence.entity.Chat
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface ChatRepository : R2dbcRepository<Chat, Long> {

    fun findAllByChatroomId(chatroomId: Long?): Flux<Chat>
    fun deleteAllByChatroomId(chatroomId: Long?): Flux<Void>
}
