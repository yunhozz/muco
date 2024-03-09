package com.muco.chatservice.domain.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "chat")
data class Chat(
        @Id
        val id: Long,
        val chatroomId: Long,
        val userId: Long,
        val nickname: String,
        val content: String,
        @CreatedDate
        val createdAt: LocalDateTime,
        @LastModifiedDate
        val updatedAt: LocalDateTime
) {}
