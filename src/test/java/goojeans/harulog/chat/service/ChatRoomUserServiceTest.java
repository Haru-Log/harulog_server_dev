package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
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
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mock 객체를 주입받기 위해 사용
class ChatRoomUserServiceTest {

    @InjectMocks
    private ChatRoomUserServiceImpl chatRoomUserService;

    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("채팅방에 유저 추가")
    void create() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        Users user = new Users();
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));

        // when
        Response<Void> response = chatRoomUserService.addUser(chatRoom.getId(), user.getNickname());

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
        String roomId = UUID.randomUUID().toString();
        Long userId = 1L;
        String userNickname = "user";
        ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
        Users user = Users.builder().id(userId).nickname(userNickname).build();
        when(userRepository.findUsersByNickname(userNickname)).thenReturn(Optional.of(user));

        ChatRoomUser chatRoomUser = ChatRoomUser.create(chatRoom, user);
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.of(chatRoomUser));

        // when
        chatRoomUserService.deleteUser(roomId, userNickname);

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
        String roomId = UUID.randomUUID().toString();
        Long userId = 1L;
        String userNickname = "user";

        Users user = Users.builder().id(userId).nickname(userNickname).build();
        when(userRepository.findUsersByNickname(userNickname)).thenReturn(Optional.of(user));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId))
                .thenReturn(Optional.empty());

        // when
        // then
        BusinessException exception = Assertions.catchThrowableOfType(
                () -> chatRoomUserService.deleteUser(roomId, userNickname),
                BusinessException.class
        );

        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ResponseCode.CHAT_NO_PERMISSION);
    }

    @Test
    @DisplayName("채팅방 ID로 참여하고 있는 유저 조회")
    void findByChatRoomSuccess() {

        // given
        String roomId = UUID.randomUUID().toString();
        Users user1 = new Users();
        Users user2 = new Users();
        when(chatRoomUserRepository.findUserByChatroomId(roomId)).thenReturn(List.of(user1, user2));

        // when
        Response<List<ChatUserDTO>> response = chatRoomUserService.getUsers(roomId);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).hasSize(2);
        verify(chatRoomUserRepository).findUserByChatroomId(roomId);
    }

    @Test
    @DisplayName("유저 ID로 참여하고 있는 채팅방 조회")
    void findByUser() {

        // given
        Users user = new Users().builder()
                .id(1L)
                .nickname("user")
                .build();

        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomUserRepository.findChatRoomsByUserNickName(user.getNickname())).thenReturn(List.of(new ChatRoom(), new ChatRoom()));

        // when
        Response<List<ChatRoomDTO>> response = chatRoomUserService.getChatRooms(user.getNickname());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).hasSize(2);
        verify(chatRoomUserRepository).findChatRoomsByUserNickName(user.getNickname());

    }
}