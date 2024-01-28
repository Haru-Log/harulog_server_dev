package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.dto.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

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
        Response<ChatRoomDTO> created = chatRoomService.create();

        // then
        Assertions.assertThat(created).isNotNull(); // response가 null이 아닌지 확인
        Assertions.assertThat(created.getData()).isNotNull(); // response의 data(ChatRoomDTO)가 null이 아닌지 확인
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방이 존재할 때 조회")
    void findByRoomIdSuccess() {
        // given
        String roomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
        doReturn(Optional.of(chatRoom)).when(chatRoomRepository).findById(roomId);

        // when
        Response<ChatRoomDTO> found = chatRoomService.findByRoomId(roomId);

        // then
        Assertions.assertThat(found).isNotNull();
        Assertions.assertThat(found.getData()).isNotNull();
        verify(chatRoomRepository).findById(roomId);
    }

    @Test
    @DisplayName("채팅방이 존재하지 않을 때 조회")
    void findByRoomIdFail() {
        // given
        String roomId = UUID.randomUUID().toString();
        doReturn(Optional.empty()).when(chatRoomRepository).findById(roomId);

        // when

        // then
        Assertions.assertThatThrownBy(() -> chatRoomService.findByRoomId(roomId))
                .isInstanceOf(BusinessException.class);
        verify(chatRoomRepository).findById(roomId);
    }

    @Test
    @DisplayName("채팅방 삭제")
    void delete() {
        // given
        String roomId = UUID.randomUUID().toString();

        // when
        Response<Void> response = chatRoomService.delete(roomId);

        // then
        Assertions.assertThat(response).isNotNull();
        verify(chatRoomRepository).deleteById(roomId);
    }
}