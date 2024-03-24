package com.muco.chatservice.domain.application

import com.muco.chatservice.domain.persistence.entity.Chat
import com.muco.chatservice.domain.persistence.entity.Chatroom
import com.muco.chatservice.domain.persistence.entity.ChatroomUser
import com.muco.chatservice.domain.persistence.repository.ChatRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomRepository
import com.muco.chatservice.domain.persistence.repository.ChatroomUserRepository
import com.muco.chatservice.global.dto.request.ChatRequestDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.any
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
class ChatServiceTest {

    @InjectMocks
    private lateinit var chatService: ChatService

    @Mock
    private lateinit var chatRepository: ChatRepository

    @Mock
    private lateinit var chatroomRepository: ChatroomRepository

    @Mock
    private lateinit var chatroomUserRepository: ChatroomUserRepository

    @Test
    @DisplayName("메세지 전송")
    fun sendMessage() {
        // given
        val userId1 = 111L
        val userId2 = 222L
        val chatroomId = 123L

        val chatroom = Chatroom(chatroomId, "Test Room", LocalDateTime.now(), LocalDateTime.now())
        val chatroomUser1 = ChatroomUser(1L, chatroom.id!!, userId1, LocalDateTime.now(), LocalDateTime.now())
        val chatroomUser2 = ChatroomUser(2L, chatroom.id!!, userId2, LocalDateTime.now(), LocalDateTime.now())

        val dto = ChatRequestDTO("This is test!!")
        val chat = Chat(1L, chatroom.id!!, userId1, userId2, "Tester", "", dto.content, LocalDateTime.now(), LocalDateTime.now())

        given(chatroomRepository.findById(anyLong()))
            .willReturn(Mono.just(chatroom))

        given(chatroomUserRepository.findAllByChatroomId(anyLong()))
            .willReturn(Flux.just(chatroomUser1, chatroomUser2))

        given(chatRepository.save(any(Chat::class.java)))
            .willReturn(Mono.just(chat))

        // when
        val result: Mono<ChatRequestDTO> = chatService.sendMessage(userId1, chatroomId, dto)

        // then
        create(result)
            .assertNext {
                assertThat(it.content).isEqualTo("This is test!!")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("특정 채팅방에 대한 채팅 목록 조회")
    fun findAllChatListByChatroom() {
        // given
        val chatroomId = 123L
        val chat1 = Chat(
            1L,
            chatroomId,
            111L,
            222L,
            "Tester",
            "",
            "This is test 1",
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now()
        )
        val chat2 = Chat(
            2L,
            chatroomId,
            111L,
            222L,
            "Tester",
            "",
            "This is test 2",
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now()
        )
        val chat3 = Chat(
            3L,
            chatroomId,
            111L,
            222L,
            "Tester",
            "",
            "This is test 3",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now()
        )

        val chatListSortedByCreatedAtDesc = listOf(chat1, chat2, chat3).stream()
            .sorted(Comparator.comparing<Chat, LocalDateTime> { chat -> chat.createdAt }.reversed())
            .toList() // chat3 -> chat2 -> chat1

        given(chatRepository.findAllByChatroomIdOrderByCreatedAtDesc(anyLong()))
            .willReturn(Flux.fromIterable(chatListSortedByCreatedAtDesc))

        // when
        val result: Flux<Chat> = chatService.findAllChatListByChatroom(chatroomId)

        // then
        create(result)
            .expectNext(chat3)
            .expectNext(chat2)
            .expectNext(chat1)
            .verifyComplete()
    }
}