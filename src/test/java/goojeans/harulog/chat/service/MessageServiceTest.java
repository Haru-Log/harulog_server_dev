package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
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
        chatRoom = ChatRoom.createDM();
    }

    @Test
    @DisplayName("채팅방 메세지 조회")
    void getMessages() {
        // given
        String roomId = chatRoom.getId();
        String userNickname = user.getNickname();

        Message message1 = Message.create(chatRoom, user, MessageType.ENTER, test);
        Message message2 = Message.create(chatRoom, user, MessageType.TALK, test);

        when(userRepository.findUsersByNickname(userNickname)).thenReturn(java.util.Optional.of(user));
        when(chatRoomRepository.findById(roomId)).thenReturn(java.util.Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));
        when(messageRepository.findByChatRoomId(roomId)).thenReturn(List.of(message1, message2));

        // when
         Response<MessageListDTO> response = messageService.getMessages(roomId, userNickname);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(ResponseCode.SUCCESS.getStatus());
        Assertions.assertThat(response.getData()).isNotNull();
        Assertions.assertThat(response.getData().getRoomId()).isEqualTo(roomId);
        Assertions.assertThat(response.getData().getMessages().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("채팅방 구독")
    void subscribe() {
        // given
        ChatRoom chatRoom = ChatRoom.createDM();
        Users user = new Users();
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(java.util.Optional.of(chatRoom));
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(java.util.Optional.of(user));

        // when
        messageService.subscribe(chatRoom.getId(), user.getNickname());

        // then
        verify(chatRoomUserRepository).save(any(ChatRoomUser.class));
    }

    @Test
    @DisplayName("채팅방 입장")
    void enter() {
        // given
        ChatRoom chatRoom = ChatRoom.createDM();
        Users user = new Users();
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        MessageDTO message = messageService.enter(chatRoom.getId(), user.getNickname());

        // then
        Assertions.assertThat(message).isNotNull();
        Assertions.assertThat(message.getContent()).isNotNull();
    }

    @Test
    @DisplayName("메세지 전송 - 성공")
    void sendSuccess() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        MessageDTO message = messageService.send(chatRoom.getId(), user.getNickname(), test);

        // then
        verify(messageRepository).save(any(Message.class));
        Assertions.assertThat(message).isNotNull();
        Assertions.assertThat(message.getContent()).isNotNull();

    }

    @Test
    @DisplayName("메세지 전송 - 실패")
    void sendFail() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.empty());

        // when
        // then
        BusinessException exception = Assertions.catchThrowableOfType(
                () -> messageService.send(chatRoom.getId(), user.getNickname(), test),
                BusinessException.class
        );

        Assertions.assertThat(exception).isNotNull();
//        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ResponseCode.CHAT_NO_PERMISSION);
    }

    @Test
    @DisplayName("채팅방 퇴장")
    void exit() {
        // given
        when(userRepository.findUsersByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(chatRoom.getId())).thenReturn(Optional.of(chatRoom));
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(chatRoom.getId(), user.getId()))
                .thenReturn(Optional.of(ChatRoomUser.create(chatRoom, user)));

        // when
        MessageDTO message = messageService.exit(chatRoom.getId(), user.getNickname());

        // then
        verify(chatRoomUserRepository).delete(any(ChatRoomUser.class));
        Assertions.assertThat(message).isNotNull();
        Assertions.assertThat(message.getContent()).isNotNull();
    }
}