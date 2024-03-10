package com.muco.chatservice.domain.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "chat")
data class Chat(
        @Id
        val id: Long? = null,
        var chatroomId: Long,
        var userId: Long,
        var nickname: String,
        var content: String,
        @CreatedDate
        val createdAt: LocalDateTime? = null,
        @LastModifiedDate
        var updatedAt: LocalDateTime? = null
) {}