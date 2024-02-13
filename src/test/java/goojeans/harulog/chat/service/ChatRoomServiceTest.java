package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.config.RabbitMQConfig;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * ChatRoomService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChatRoomUserRepository chatRoomUserRepository;
    @Mock private RabbitMQConfig rabbitMQConfig;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    private String userNickname1 = "test1";
    private String userNickname2 = "test2";

    @Test
    @DisplayName("채팅방 생성")
    void createChatRoom() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        doReturn(chatRoom).when(chatRoomRepository).save(any(ChatRoom.class));

        Users user1 = Users.builder().nickname(userNickname1).build();
        Users user2 = Users.builder().nickname(userNickname2).build();
        when(userRepository.findUsersByNickname(userNickname1)).thenReturn(Optional.of(user1));
        when(userRepository.findUsersByNickname(userNickname2)).thenReturn(Optional.of(user2));

        // when
        Response<ChatRoomDTO> created = chatRoomService.createChatRoom(List.of(userNickname1, userNickname2));

        // then
        Assertions.assertThat(created).isNotNull(); // response가 null이 아닌지 확인
        Assertions.assertThat(created.getData()).isNotNull(); // response의 data(ChatRoomDTO)가 null이 아닌지 확인
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 생성 실패 - 유저가 2명 미만일 때")
    void createChatRoomFail() {
        // given
        Users user1 = Users.builder().nickname(userNickname1).build();

        // when

        // then
        // 채팅방 생성 실패 시의 에러 메세지 확인
        Assertions.assertThatThrownBy(() -> chatRoomService.createChatRoom(List.of(userNickname1)))
                .isInstanceOf(BusinessException.class)   // BusinessException이 발생하는지 확인
                .hasMessageContaining(ResponseCode.CHATROOM_USER_NOT_ENOUGH.getMessage()); // BusinessException의 메세지가 "채팅방에 참여한 유저가 2명 미만입니다."인지 확인
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
        // 채팅방이 존재하지 않을 때의 에러 메세지 확인
        Assertions.assertThatThrownBy(() -> chatRoomService.findByRoomId(roomId))
                .isInstanceOf(BusinessException.class)                               // BusinessException이 발생하는지 확인
                .hasMessageContaining(ResponseCode.CHATROOM_NOT_FOUND.getMessage()); // BusinessException의 메세지가 "채팅방을 찾을 수 없습니다."인지 확인

        verify(chatRoomRepository).findById(roomId);
    }

    @Test
    @DisplayName("채팅방 삭제")
    void delete() {
        // given
        String roomId = UUID.randomUUID().toString();

        // when
        Response<Void> response = chatRoomService.deleteChatRoom(roomId);

        // then
        Assertions.assertThat(response).isNotNull();
        verify(chatRoomRepository).deleteById(roomId);
    }
}