package com.muco.chatservice.domain.persistence.repository

import com.muco.chatservice.domain.persistence.entity.ChatroomUser
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ChatroomUserRepository : R2dbcRepository<ChatroomUser, Long>
