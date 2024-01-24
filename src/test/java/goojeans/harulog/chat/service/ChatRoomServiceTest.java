package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.mockito.Mockito.verify;

/**
 * ChatRoomService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    @Test
    @DisplayName("채팅방 생성")
    void createChatRoom() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        doReturn(chatRoom).when(chatRoomRepository).save(any(ChatRoom.class));
//        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        // when
        ChatRoomDTO created = chatRoomService.createChatRoom();

        // then
        Assertions.assertThat(created).isNotNull();
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방이 존재할 때 조회")
    void findByRoomIdSuccess() {
        // given
        Long roomId = 1L;
        ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
        doReturn(Optional.of(chatRoom)).when(chatRoomRepository).findById(roomId);

        // when
        ChatRoomDTO found = chatRoomService.findByRoomId(roomId);

        // then
        Assertions.assertThat(found).isNotNull();
        verify(chatRoomRepository).findById(roomId);
    }

    @Test
    @DisplayName("채팅방이 존재하지 않을 때 조회")
    void findByRoomIdFail() {
        // given
        Long roomId = 1L;
        doReturn(Optional.empty()).when(chatRoomRepository).findById(roomId);

        // when

        // then
        Assertions.assertThatThrownBy(() -> chatRoomService.findByRoomId(roomId))
                .isInstanceOf(Exception.class);
        verify(chatRoomRepository).findById(roomId);
    }

    @Test
    @DisplayName("채팅방이 존재할 때 삭제")
    void deleteChatRoomSuccess() {
        // given
        Long roomId = 1L;
        ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
        doReturn(Optional.of(chatRoom)).when(chatRoomRepository).findById(roomId);

        // when
        chatRoomService.deleteChatRoom(roomId);

        // then
        verify(chatRoomRepository).deleteById(roomId);
    }

    @Test
    @DisplayName("채팅방이 존재하지 않을 때 삭제")
    void deleteChatRoomFail() {
        // given
        Long roomId = 1L;
        doReturn(Optional.empty()).when(chatRoomRepository).findById(roomId);

        // when

        // then
        Assertions.assertThatThrownBy(() -> chatRoomService.deleteChatRoom(roomId))
                .isInstanceOf(Exception.class);
        verify(chatRoomRepository).findById(roomId);
    }
}