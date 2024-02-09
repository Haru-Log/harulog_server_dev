package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.chat.repository.MessageRepository;
import goojeans.harulog.chat.util.MessageType;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mock 객체를 주입받기 위해 사용
class ChatRoomUserServiceTest {

    @InjectMocks
    private ChatRoomUserServiceImpl chatRoomUserService;

    @Mock private ChatRoomUserRepository chatRoomUserRepository;
    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private RabbitMQConfig rabbitMQConfig;

    @Captor
    private ArgumentCaptor<Message> messageCaptor; // 저장되는 Message 캡처를 위한 ArgumentCaptor

    @Test
    @DisplayName("채팅방에 유저 추가")
    void addUser() {

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
    @DisplayName("채팅방에 유저 여러멍 추가")
    void addUsers() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        Users user1 = Users.builder().nickname("user1").build();
        Users user2 = Users.builder().nickname("user2").build();
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(userRepository.findUsersByNickname(user1.getNickname())).thenReturn(Optional.of(user1));
        when(userRepository.findUsersByNickname(user2.getNickname())).thenReturn(Optional.of(user2));

        // when
        Response<Void> response = chatRoomUserService.addUser(chatRoom.getId(), List.of(user1.getNickname(), user2.getNickname()));

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());

        // chatRoomUserRepository.save 메서드가 몇번 호출되었는지 검증
        verify(chatRoomUserRepository, times(2)).save(any(ChatRoomUser.class));
    }

    @Test
    @DisplayName("채팅방에 참여 중인 유저 삭제")
    void deleteUserSuccess() {

        // given
        String roomId = UUID.randomUUID().toString();
        Long userId = 1L;
        String userNickname = "user";
        ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
        Users user = Users.builder().id(userId).nickname(userNickname).build();
        when(userRepository.findUsersByNickname(userNickname)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));

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
    void deleteUserFail() {

        // given
        String roomId = UUID.randomUUID().toString();
        Long userId = 1L;
        String userNickname = "user";

        Users user = Users.builder().id(userId).nickname(userNickname).build();
        ChatRoom chatRoom = ChatRoom.builder().id(roomId).build();
        when(userRepository.findUsersByNickname(userNickname)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
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
    void getUsers() {

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
    void getChatRooms() {

        // given
        Users user = Users.builder()
                .id(1L)
                .nickname("user")
                .build();

        when(chatRoomUserRepository.findChatRoomsByUserNickName(user.getNickname())).thenReturn(List.of(new ChatRoom(), new ChatRoom()));

        // when
        Response<List<ChatRoomDTO>> response = chatRoomUserService.getChatRooms(user.getNickname());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).hasSize(2);
        verify(chatRoomUserRepository).findChatRoomsByUserNickName(user.getNickname());

    }

    @Test
    @DisplayName("채팅방 입장 메세지 생성, 저장 및 전송")
    void sendEnterMessage() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        Users user = new Users();
        ChatRoomUser chatRoomUser = ChatRoomUser.create(chatRoom, user);

        // when
        chatRoomUserService.sendEnterMessage(chatRoom, user);

        // then
        verify(messageRepository).save(messageCaptor.capture()); // Message 객체 캡처
        Message captoredMessage = messageCaptor.getValue();
        Assertions.assertThat(captoredMessage.getType()).isEqualTo(MessageType.ENTER); // 입장 메세지가 만들어진 것인지 확인.

        verify(rabbitTemplate).convertAndSend(eq("chatroom."+chatRoom.getId()), eq(""), any(MessageDTO.class));
    }

    @Test
    @DisplayName("채팅방 퇴장 메세지 생성, 저장 및 전송")
    void sendExitMessage() {

        // given
        ChatRoom chatRoom = new ChatRoom();
        Users user = new Users();
        ChatRoomUser chatRoomUser = ChatRoomUser.create(chatRoom, user);

        // when
        chatRoomUserService.sendExitMessage(chatRoom, user);

        // then
        verify(messageRepository).save(messageCaptor.capture()); // Message 객체 캡처
        Message captoredMessage = messageCaptor.getValue();
        Assertions.assertThat(captoredMessage.getType()).isEqualTo(MessageType.EXIT); // 퇴장 메세지가 만들어진 것인지 확인.

        verify(rabbitTemplate).convertAndSend(eq("chatroom."+chatRoom.getId()), eq(""), any(MessageDTO.class));
    }
}