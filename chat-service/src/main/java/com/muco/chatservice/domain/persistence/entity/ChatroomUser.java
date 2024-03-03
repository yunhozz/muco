package com.muco.chatservice.domain.persistence.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "chatroom_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatroomUser {

    @Id
    private Long id;

    private Long chatroomId;

    private Long userId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public ChatroomUser(Long chatroomId, Long userId) {
        this.chatroomId = chatroomId;
        this.userId = userId;
    }
}
