package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
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
    void delete() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        Users user = new Users();
        ChatRoomUser chatRoomUser = ChatRoomUser.create(chatRoom, user);
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(chatRoomUser));

        // when
        chatRoomUserService.delete(chatRoom.getId(), user.getId());

        // then
        verify(chatRoomUserRepository).delete(any(ChatRoomUser.class));

    }

    @Test
    @DisplayName("채팅방 ID로 참여하고 있는 유저 조회")
    void findByChatRoomSuccess() {

        // given
        Users user1 = new Users();
        Users user2 = new Users();
        when(chatRoomUserRepository.findUserByChatroomId(anyLong())).thenReturn(List.of(user1, user2));

        // when
        Response response = chatRoomUserService.findByChatRoom(1L);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).isNotNull();
        verify(chatRoomUserRepository).findUserByChatroomId(anyLong());
    }

    @Test
    @DisplayName("유저 ID로 참여하고 있는 채팅방 조회")
    void findByUser() {

        // given
        Long userId = 1L;
        when(chatRoomUserRepository.findChatRoomsByUserId(anyLong())).thenReturn(List.of(new ChatRoom(), new ChatRoom()));

        // when
        Response response = chatRoomUserService.findByUser(userId);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).isNotNull();
        verify(chatRoomUserRepository).findChatRoomsByUserId(userId);

    }
}