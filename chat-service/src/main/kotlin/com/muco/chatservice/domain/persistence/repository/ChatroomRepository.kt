package com.muco.chatservice.domain.persistence.repository

import com.muco.chatservice.domain.persistence.entity.Chatroom
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface ChatroomRepository: R2dbcRepository<Chatroom, Long> {

    fun findAllByIdOrderByUpdatedAtAsc(id: Long?): Flux<Chatroom>
}
