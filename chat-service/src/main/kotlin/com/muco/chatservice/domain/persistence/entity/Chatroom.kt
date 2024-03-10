package com.muco.chatservice.domain.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Table(name = "chatroom")
data class Chatroom(
        @Id
        val id: Long? = null,
        var name: String,
        @CreatedDate
        val createdAt: LocalDateTime? = null,
        @LastModifiedDate
        var updatedAt: LocalDateTime? = null
) {
        fun updateName(name: String): Mono<Chatroom> {
                this.name = name
                return Mono.just(this)
        }
}
