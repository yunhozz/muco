package com.muco.chatservice.domain.persistence.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "chatroom")
data class Chatroom(
        @Id
        val id: Long,
        val name: String,
        @CreatedDate
        val createdAt: LocalDateTime,
        @LastModifiedDate
        val updatedAt: LocalDateTime
) {}
