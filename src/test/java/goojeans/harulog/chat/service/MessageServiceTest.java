package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.chat.repository.MessageRepository;
import goojeans.harulog.chat.util.MessageType;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mock 객체를 주입받기 위해 사용
class MessageServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ChatRoomRepository chatRoomRepository;
    @Mock private ChatRoomUserRepository chatRoomUserRepository;
    @Mock private MessageRepository messageRepository;
    @InjectMocks private MessageServiceImpl messageService;

    String test = "test";

    private Users user;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        user = new Users();
        chatRoom = ChatRoom.create(test);
    }

    @Test
    @DisplayName("채팅방 메세지 조회")
    void getMessages() {
        // given
        Message message1 = Message.create(chatRoom, user, MessageType.ENTER, test);
        Message message2 = Message.create(chatRoom, user, MessageType.TALK, test);
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(java.util.Optional.of(chatRoom));
        when(messageRepository.findByChatRoomId(chatRoom.getId())).thenReturn(List.of(message1, message2));

        // when
         Response<List<MessageDTO>> response = messageService.getMessages(chatRoom.getId());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
        Assertions.assertThat(response.getData().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("채팅방 입장")
    void enter() {
        // given
        ChatRoom chatRoom = ChatRoom.create(test);
        Users user = new Users();
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(java.util.Optional.of(chatRoom));
        when(userRepository.findById(user.getId())).thenReturn(java.util.Optional.of(user));

        // when
        Response<MessageDTO> response = messageService.enter(chatRoom.getId(), user.getId());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
    }

    @Test
    @DisplayName("채팅방 입장 - 이미 참여 중인 유저")
    void enter_AlreadyEnter() {
        // given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        Response<MessageDTO> response = messageService.enter(chatRoom.getId(), user.getId());

        // then
        // 응답에 data값은 없지만 success 해야함.
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
        Assertions.assertThat(response.getData()).isNull();

    }

    @Test
    @DisplayName("메세지 전송 - 성공")
    void sendSuccess() {
        // given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        Response<MessageDTO> response = messageService.send(chatRoom.getId(), user.getId(), test);

        // then
        verify(messageRepository).save(any(Message.class));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).isNotNull();

    }

    @Test
    @DisplayName("메세지 전송 - 실패")
    void sendFail() {
        // given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // when
        // then
        BusinessException exception = Assertions.catchThrowableOfType(
                () -> messageService.send(chatRoom.getId(), user.getId(), test),
                BusinessException.class
        );

        Assertions.assertThat(exception).isNotNull();
//        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ResponseCode.CHAT_NO_PERMISSION);
    }

    @Test
    @DisplayName("채팅방 퇴장")
    void exit() {
        // given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        Response<MessageDTO> response = messageService.exit(chatRoom.getId(), user.getId());

        // then
        verify(chatRoomUserRepository).delete(any(ChatRoomUser.class));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).isNotNull();
    }

    @Test
    @DisplayName("채팅방 퇴장 - 이미 퇴장한 유저")
    void exit_AlreadyExit() {
        // given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // when
        Response<MessageDTO> response = messageService.exit(chatRoom.getId(), user.getId());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).isNull();
    }
}