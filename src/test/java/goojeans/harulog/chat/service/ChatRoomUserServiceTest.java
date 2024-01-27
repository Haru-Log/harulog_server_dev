package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mock 객체를 주입받기 위해 사용
class ChatRoomUserServiceTest {

    @InjectMocks
    private ChatRoomUserServiceImpl chatRoomUserService;

    @Mock private ChatRoomUserRepository chatRoomUserRepository;
    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private UserRepository userRepository;

    @Test
    @DisplayName("채팅방에 유저 추가")
    void create() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        Users user = new Users();
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        Response<Void> response = chatRoomUserService.create(chatRoom.getId(), user.getId());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());

        // chatRoomUserRepository.save 메서드가 호출되었는지 검증
        verify(chatRoomUserRepository).save(any(ChatRoomUser.class));
    }

    @Test
    @DisplayName("채팅방에 참여 중인 유저 삭제")
    void deleteSuccess() {

        // given
        Long roomId = 1L;
        Long userId = 1L;
        ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
        Users user = Users.builder().id(userId).build();

        ChatRoomUser chatRoomUser = ChatRoomUser.create(chatRoom, user);
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.of(chatRoomUser));

        // when
        chatRoomUserService.delete(roomId, userId);

        // then
        /**
         * chatRoomUserRepository.delete 메서드가 호출되었는지 검증
         * chatRoomUserRepository.delete 메서드의 인자로 ChatRoomUser 객체가 전달되었는지 검증
         */
        verify(chatRoomUserRepository).delete(argThat(a ->
                a.getChatRoom().getId().equals(chatRoom.getId()) &&
                a.getUser().getId().equals(user.getId())
        ));
    }

    @Test
    @DisplayName("존재하지 않는 채팅방-유저 삭제 시 예외 발생")
    void deleteFail() {

        // given
        Long roomId = 1L;
        Long userId = 1L;

        when(chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.empty());

        // when
        // then
        BusinessException exception = Assertions.catchThrowableOfType(
                () -> chatRoomUserService.delete(roomId, userId),
                BusinessException.class
        );

        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ResponseCode.CHAT_NO_PERMISSION);
    }

    @Test
    @DisplayName("채팅방 ID로 참여하고 있는 유저 조회")
    void findByChatRoomSuccess() {

        // given
        Long roomId = 1L;
        Users user1 = new Users();
        Users user2 = new Users();
        when(chatRoomUserRepository.findUserByChatroomId(roomId)).thenReturn(List.of(user1, user2));

        // when
        Response response = chatRoomUserService.findByChatRoom(roomId);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).isNotNull();
        verify(chatRoomUserRepository).findUserByChatroomId(roomId);
    }

    @Test
    @DisplayName("유저 ID로 참여하고 있는 채팅방 조회")
    void findByUser() {

        // given
        Long userId = 1L;
        when(chatRoomUserRepository.findChatRoomsByUserId(userId)).thenReturn(List.of(new ChatRoom(), new ChatRoom()));

        // when
        Response response = chatRoomUserService.findByUser(userId);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).isNotNull();
        verify(chatRoomUserRepository).findChatRoomsByUserId(userId);

    }
}