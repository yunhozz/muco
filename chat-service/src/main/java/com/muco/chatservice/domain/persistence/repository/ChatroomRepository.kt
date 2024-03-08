package com.muco.chatservice.domain.persistence.repository;

import com.muco.chatservice.domain.persistence.entity.Chatroom;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ChatroomRepository extends R2dbcRepository<Chatroom, Long> {
}
