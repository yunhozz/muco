package com.muco.chatservice.domain.application

import com.muco.chatservice.domain.persistence.entity.Chat
import com.muco.chatservice.domain.persistence.entity.Chatroom
import com.muco.chatservice.domain.persistence.entity.ChatroomUser
import com.muco.chatservice.domain.persistence.repository.ChatRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomUserRepository
import com.muco.chatservice.global.dto.request.CreateChatroomRequestDTO
import com.muco.chatservice.global.dto.request.UpdateChatroomRequestDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.anyList
import org.mockito.BDDMockito.anyLong
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier.create
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ChatroomServiceTest {

    @InjectMocks
    private lateinit var chatroomService: ChatroomService

    @Mock
    private lateinit var chatRepository: ChatRepository

    @Mock
    private lateinit var chatroomRepository: ChatroomRepository

    @Mock
    private lateinit var chatroomUserRepository: ChatroomUserRepository

    @Test
    @DisplayName("채팅방 생성")
    fun makeChatroom() {
        // given
        val userId = 1L
        val partnerId = 2L

        val chatroomId = 123L
        val chatroomName = "This is test"
        val chatroom = Chatroom(chatroomId, name = chatroomName, LocalDateTime.now(), LocalDateTime.now())

        val me = ChatroomUser(chatroomId = chatroomId, userId = userId)
        val partner = ChatroomUser(chatroomId = chatroomId, userId = partnerId)

        given(chatroomRepository.save(any(Chatroom::class.java)))
            .willReturn(Mono.just(chatroom))

        given(chatroomUserRepository.saveAll(anyList()))
            .willReturn(Flux.just(me, partner))

        // when
        val dto = CreateChatroomRequestDTO(partnerId, name = chatroomName)
        val result: Mono<Chatroom> = chatroomService.makeChatroom(userId, dto)

        // then
        create(result)
            .assertNext {
                assertThat(it.id).isEqualTo(chatroomId)
                assertThat(it.name).isEqualTo(chatroomName)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("채팅방 업데이트")
    fun updateChatroom() {
        // given
        val chatroomId = 1L
        val originalName = "This is test"
        val updateName = "This is updated"

        val chatroom = Chatroom(chatroomId, name = originalName, LocalDateTime.now(), LocalDateTime.now())
        val updatedChatroom = Chatroom(chatroomId, name = updateName, chatroom.createdAt, LocalDateTime.now())

        given(chatroomRepository.findById(anyLong()))
            .willReturn(Mono.just(chatroom))

        given(chatroomRepository.save(any(Chatroom::class.java)))
            .willReturn(Mono.just(updatedChatroom))

        // when
        val dto = UpdateChatroomRequestDTO(name = updateName)
        val result: Mono<Chatroom> = chatroomService.updateChatroom(chatroomId, dto)

        // then
        create(result)
            .assertNext {
                assertThat(it.name).isEqualTo(updateName)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("특정 유저의 채팅방 목록 조회")
    fun findChatroomListByUserId() {
        // given
        val userId = 123L

        val chatroom1 = Chatroom(1L, "This is test1", LocalDateTime.now(), LocalDateTime.now())
        val chatroom2 = Chatroom(2L, "This is test2", LocalDateTime.now(), LocalDateTime.now())
        val chatroom3 = Chatroom(3L, "This is test3", LocalDateTime.now(), LocalDateTime.now())

        val chatroomUser1 = ChatroomUser(1L, chatroom1.id!!, userId, LocalDateTime.now(), LocalDateTime.now())
        val chatroomUser2 = ChatroomUser(2L, chatroom2.id!!, userId, LocalDateTime.now(), LocalDateTime.now())

        given(chatroomUserRepository.findByUserId(anyLong()))
            .willReturn(Flux.fromIterable(listOf(chatroomUser1, chatroomUser2)))

        given(chatroomRepository.findById(chatroomUser1.chatroomId))
            .willReturn(Mono.just(chatroom1))

        given(chatroomRepository.findById(chatroomUser2.chatroomId))
            .willReturn(Mono.just(chatroom2))

        // when
        val result: Flux<Chatroom> = chatroomService.findChatroomListByUserId(userId)

        // then
        create(result)
            .expectNext(chatroom1)
            .expectNext(chatroom2)
            .verifyComplete()
    }

    @Test
    @DisplayName("채팅방 삭제")
    fun deleteChatroom() {
        // given
        val chatroomId = 1L
        val senderId = 111L
        val receiverId = 222L

        val chatroom = Chatroom(chatroomId, "This is test", LocalDateTime.now(), LocalDateTime.now())
        val sender = ChatroomUser(id = 1L, chatroomId = chatroomId, userId = senderId)
        val receiver = ChatroomUser(id = 2L, chatroomId = chatroomId, userId = receiverId)

        val chat1 = Chat(
            1L,
            chatroomId,
            senderId,
            receiverId,
            "tester",
            "test-image",
            "test message 1",
            LocalDateTime.now(),
            LocalDateTime.now()
        )
        val chat2 = Chat(
            2L,
            chatroomId,
            senderId,
            receiverId,
            "tester",
            "test-image",
            "test message 2",
            LocalDateTime.now(),
            LocalDateTime.now()
        )
        val chat3 = Chat(
            3L,
            chatroomId,
            senderId,
            receiverId,
            "tester",
            "test-image",
            "test message 3",
            LocalDateTime.now(),
            LocalDateTime.now()
        )

        given(chatroomRepository.findById(anyLong()))
            .willReturn(Mono.just(chatroom))

        given(chatRepository.findAllByChatroomIdOrderByCreatedAtDesc(anyLong()))
            .willReturn(Flux.fromIterable(listOf(chat1, chat2, chat3)))

        given(chatRepository.deleteById(anyLong()))
            .willReturn(Mono.empty())

        given(chatroomUserRepository.findAllByChatroomId(anyLong()))
            .willReturn(Flux.just(sender, receiver))

        given(chatroomUserRepository.deleteById(anyLong()))
            .willReturn(Mono.empty())

        given(chatroomRepository.deleteById(anyLong()))
            .willReturn(Mono.empty())

        // when
        val result: Mono<Void> = chatroomService.deleteChatroom(chatroomId)

        // then
        create(result)
            .expectSubscription()
            .verifyComplete()
    }
}