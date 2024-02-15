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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.*;

import static goojeans.harulog.chat.util.ChatRoomType.CHALLENGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private RabbitMQConfig rabbitMQConfig;

    @Captor
    private ArgumentCaptor<Message> messageCaptor; // 저장되는 Message 캡처를 위한 ArgumentCaptor

    String roomId = "roomId";
    Long userId = 1L;
    Long userId2 = 2L;

    ChatRoom room;
    Users user1, user2;

    @BeforeEach
    void setUp() {
        user1 = Users.builder().id(userId).nickname("user1").build();
        user2 = Users.builder().id(userId2).nickname("user2").build();
        room = ChatRoom.builder().id(roomId).build();
    }

    @Test
    @DisplayName("채팅방에 유저 추가")
    void addUser() {
        // given
        // when
        chatRoomUserService.addUser(room, user1);

        // then
        verify(chatRoomUserRepository).save(any(ChatRoomUser.class));
    }

    @Test
    @DisplayName("채팅방에 유저 여러명 추가")
    void addUsers() {
        // given
        // when
        chatRoomUserService.addUsers(room, List.of(user1, user2));

        // then
        verify(chatRoomUserRepository, times(2)).save(any(ChatRoomUser.class));
    }

    @Test
    @DisplayName("채팅방에 유저 삭제 - 채팅방에 유저가 없으면 채팅방 삭제")
    void deleteUser() {
        // given
        ChatRoomUser cru = ChatRoomUser.builder().chatRoom(room).user(user1).build();

        // when
        chatRoomUserService.deleteUser(cru);

        // then
        verify(chatRoomUserRepository).delete(cru);
        verify(chatRoomRepository).delete(room);
    }

    @Test
    @DisplayName("채팅방에 유저 추가 - 응답")
    void ResponseAddUser() {
        // given
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findUsersByNickname(user1.getNickname())).thenReturn(Optional.of(user1));

        // when
        Response<Void> response = chatRoomUserService.addUser(roomId, user1.getNickname());

        // then
        verify(chatRoomUserRepository).save(any(ChatRoomUser.class));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo(Response.ok().getCode());
    }

    @Test
    @DisplayName("채팅방에 유저 여러명 추가 - 응답")
    void ResponseAddUsers() {
        // given
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findUsersByNickname(user1.getNickname())).thenReturn(Optional.of(user1));
        when(userRepository.findUsersByNickname(user2.getNickname())).thenReturn(Optional.of(user2));
        ChatRoomUser cru1 = ChatRoomUser.builder().chatRoom(room).user(user1).build();
        ChatRoomUser cru2 = ChatRoomUser.builder().chatRoom(room).user(user2).build();

        // when
        Response<Void> response = chatRoomUserService.addUsers(roomId, List.of(user1.getNickname(), user2.getNickname()));

        // then
        verify(chatRoomUserRepository, times(2)).save(any(ChatRoomUser.class));
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo(Response.ok().getCode());
    }

    @Test
    @DisplayName("채팅방에 유저 삭제 - 실패: Controller에서 Challenge 채팅방을 나가려고 하는 경우")
    void deleteUserRequest() {
        // given
        ChatRoom challengeRoom = ChatRoom.builder().type(CHALLENGE).build();
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(challengeRoom));

        // when
        BusinessException thrown = Assertions.catchThrowableOfType(
                () -> chatRoomUserService.deleteUserRequest(roomId, user1.getNickname()),
                BusinessException.class
        );

        // then
        Assertions.assertThat(thrown.getErrorCode()).isEqualTo(ResponseCode.CHALLENGE_CHATROOM_USER_CANNOT_LEAVE);

    }

    @Test
    @DisplayName("채팅방에 유저 삭제 - 응답")
    void ResponseDeleteUser() {
        // given
        when(userRepository.findUsersByNickname(user1.getNickname())).thenReturn(Optional.of(user1));
        ChatRoomUser cru = ChatRoomUser.builder().chatRoom(room).user(user1).build();
        when(chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId)).thenReturn(Optional.of(cru));

        // when
        Response<Void> response = chatRoomUserService.deleteUser(roomId, user1.getNickname());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo(Response.ok().getCode());
    }

    @Test
    @DisplayName("채팅방에 참여 중인 유저 조회 - 응답")
    void getUsers() {
        // given
        when(chatRoomUserRepository.findUserByChatroomId(room.getId())).thenReturn(List.of(user1, user2));

        // when
        Response<List<ChatUserDTO>> response = chatRoomUserService.getUsers(roomId);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getCode()).isEqualTo(Response.ok().getCode());
        Assertions.assertThat(response.getData()).hasSize(2);
        verify(chatRoomUserRepository).findUserByChatroomId(room.getId());
    }

    @Test
    @DisplayName("유저가 참여하고 있는 채팅방 조회 - 응답")
    void getChatRooms() {
        // given
        when(chatRoomUserRepository.findChatRoomsByUserNickName(user1.getNickname())).thenReturn(List.of(new ChatRoom(), new ChatRoom()));

        // when
        Response<List<ChatRoomDTO>> response = chatRoomUserService.getChatRooms(user1.getNickname());

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getData()).hasSize(2);
        verify(chatRoomUserRepository).findChatRoomsByUserNickName(user1.getNickname());
    }

    @Test
    @DisplayName("입장 메세지 생성, 저장 및 전송")
    void sendEnterMessage() {
        // given
        ChatRoomUser chatRoomUser = ChatRoomUser.create(room, user1);

        // when
        chatRoomUserService.sendEnterMessage(room, user1);

        // then
        verify(messageRepository).save(messageCaptor.capture()); // Message 객체 캡처
        Message captoredMessage = messageCaptor.getValue();
        Assertions.assertThat(captoredMessage.getType()).isEqualTo(MessageType.ENTER); // 입장 메세지가 만들어진 것인지 확인.

        verify(rabbitTemplate).convertAndSend(eq("chatroom." + room.getId()), eq(""), any(MessageDTO.class));

    }

    @Test
    @DisplayName("퇴장 메세지 생성, 저장 및 전송")
    void sendExitMessage() {
        // given
        ChatRoomUser chatRoomUser = ChatRoomUser.create(room, user1);

        // when
        chatRoomUserService.sendExitMessage(room, user1);

        // then
        verify(messageRepository).save(messageCaptor.capture()); // Message 객체 캡처
        Message captoredMessage = messageCaptor.getValue();
        Assertions.assertThat(captoredMessage.getType()).isEqualTo(MessageType.EXIT); // 퇴장 메세지가 만들어진 것인지 확인.

        verify(rabbitTemplate).convertAndSend(eq("chatroom." + room.getId()), eq(""), any(MessageDTO.class));
    }
}