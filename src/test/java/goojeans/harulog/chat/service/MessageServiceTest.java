package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.chat.repository.MessageRepository;
import goojeans.harulog.config.RabbitMQConfig;
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
    @Mock private RabbitMQConfig rabbitMQConfig;

    @InjectMocks private MessageServiceImpl messageService;

    String test = "test";

    private Users user;
    private ChatRoom chatRoom;
    private Message message1, message2, message3, message4, message5;

    @BeforeEach
    void setUp() {
        user = new Users();
        chatRoom = ChatRoom.createDM();
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));

        message1 = Message.create(chatRoom, user, test);
        message2 = Message.create(chatRoom, user, test);
        message3 = Message.create(chatRoom, user, test);
        message4 = Message.create(chatRoom, user, test);
        message5 = Message.create(chatRoom, user, test);
    }

    @Test
    @DisplayName("채팅방 이전 메세지 조회")
    void getMessagesBeforeResponse() {
        // given
        when(messageRepository.findBeforeMessagesWithPagination(chatRoom.getId(), message5.getId(), 30))
                .thenReturn(List.of(message4, message3, message2, message1));

        // when
        Response<MessageListDTO> response = messageService.getMessagesBeforeResponse(chatRoom.getId(), message5.getId());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
        Assertions.assertThat(response.getData()).isNotNull();
        Assertions.assertThat(response.getData().getMessages()).isNotNull();
        Assertions.assertThat(response.getData().getMessages().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("채팅방 이후 메세지 조회")
    void getMessagesAfterResponse() {
        // given
        when(messageRepository.findAfterMessagesWithPagination(chatRoom.getId(), message1.getId(), 30))
                .thenReturn(List.of(message2, message3, message4, message5));

        // when
        Response<MessageListDTO> response = messageService.getMessagesAfterResponse(chatRoom.getId(), message1.getId());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
        Assertions.assertThat(response.getData()).isNotNull();
        Assertions.assertThat(response.getData().getMessages()).isNotNull();
        Assertions.assertThat(response.getData().getMessages().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("채팅방 들어가기")
    void roomIn() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        Response<MessageListDTO> response = messageService.roomIn(chatRoom.getId(), user.getNickname());

        // then
        verify(rabbitMQConfig).binding(chatRoom.getId(), user.getNickname());
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
        Assertions.assertThat(response.getData()).isNotNull();
        Assertions.assertThat(response.getData().getRoomId()).isEqualTo(chatRoom.getId());
    }

    @Test
    @DisplayName("채팅방 들어가기 - 실패")
    void roomInFail() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = Assertions.catchThrowableOfType(
                () -> messageService.roomIn(chatRoom.getId(), user.getNickname()),
                BusinessException.class
        );

        // then
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ResponseCode.CHAT_NO_PERMISSION);
    }

    @Test
    @DisplayName("채팅방 나가기")
    void roomOut() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));
        when(messageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId())).thenReturn(message1);

        // when
        Response<Void> response = messageService.roomOut(chatRoom.getId(), user.getNickname());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
    }

    @Test
    @DisplayName("채팅방 나가기 - 실패")
    void roomOutFail() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = Assertions.catchThrowableOfType(
                () -> messageService.roomOut(chatRoom.getId(), user.getNickname()),
                BusinessException.class
        );

        // then
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ResponseCode.CHAT_NO_PERMISSION);
    }

    @Test
    @DisplayName("메세지 전송")
    void sendMessage() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        MessageDTO message = messageService.sendMessage(chatRoom.getId(), user.getNickname(), test);

        // then
        verify(messageRepository).save(any(Message.class));
        Assertions.assertThat(message).isNotNull();
        Assertions.assertThat(message.getContent()).isNotNull();
    }

    @Test
    @DisplayName("메세지 전송 - 실패")
    void sendMessageFail() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = Assertions.catchThrowableOfType(
                () -> messageService.sendMessage(chatRoom.getId(), user.getNickname(), test),
                BusinessException.class
        );

        // then
        Assertions.assertThat(exception).isNotNull();
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ResponseCode.CHAT_NO_PERMISSION);
    }
}