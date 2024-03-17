package com.muco.chatservice.domain.persistence.repository

import com.muco.chatservice.domain.persistence.entity.ChatroomUser
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface ChatroomUserRepository: R2dbcRepository<ChatroomUser, Long> {

    fun findAllByChatroomId(chatroomId: Long?): Flux<ChatroomUser>
}
