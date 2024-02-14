package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.util.ChatRoomType;
import goojeans.harulog.config.RabbitMQConfig;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final RabbitMQConfig rabbitMQConfig;

    private final ChatRoomUserService chatRoomUserService;

    /**
     * 채팅방 생성
     * + 채팅방 생성 시 exchange 생성
     */
    @Override
    public Response<ChatRoomDTO> createChatRoom(List<String> nicknames) {
        log.trace("create ChatRoom");

        ChatRoom chatRoom = ChatRoom.createDM();
        chatRoomRepository.save(chatRoom);

        // 채팅방 생성 시 exchange 생성
        rabbitMQConfig.createFanoutExchange(chatRoom.getId());

        // 닉네임이 2개 미만이면 채팅방 생성 불가 (자기 자신만 있는 채팅방 생성 불가)
        if(nicknames.size() < 2){
            throw new BusinessException(ResponseCode.CHATROOM_USER_NOT_ENOUGH);
        }

        // 닉네임으로 유저 찾아오기
        List<Users> users = nicknames.stream()
                .map(this::findUser)
                .toList();

        // 채팅방에 유저 추가
        chatRoomUserService.addUser(chatRoom, users);

        // 채팅방에 참여한 유저가 2명이상이면 Group 채팅방으로 변경
        if(users.size()>2){
            chatRoom.setType(ChatRoomType.GROUP);
        }
        chatRoomRepository.save(chatRoom);

        return Response.ok(ChatRoomDTO.of(chatRoom));
    }

    @Override
    public ChatRoom createChallengeChatRoom(String challengeName, String imageUrl) {
        log.trace("createChallengeChatRoom() execute");

        // 채팅방 생성
        ChatRoom room = ChatRoom.createChallenge(challengeName, imageUrl);

        // 채팅방 생성 시 exchange 생성
        rabbitMQConfig.createFanoutExchange(room.getId());

        // 채팅방 저장
        return chatRoomRepository.save(room);
    }

    /**
     * 채팅방 조회
     */
    @Override
    public Response<ChatRoomDTO> findByRoomId(String roomId) {
        log.trace("findByRoomId() execute");

        ChatRoom find = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_NOT_FOUND));
        return Response.ok(ChatRoomDTO.of(find));
    }

    /**
     * 채팅방 삭제 (soft delete)
     * + 채팅방 삭제 시 exchange 삭제
     */
    @Override
    public Response<Void> deleteChatRoom(String roomId) {
        log.trace("deleteChatRoom() execute");

        // 채팅방 삭제 시 exchange 삭제
        rabbitMQConfig.deleteExchange(roomId);

        // 채팅방 삭제
        chatRoomRepository.deleteById(roomId);

        return Response.ok();
    }

    private Users findUser(String userNickname) {
        return userRepository.findUsersByNickname(userNickname)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }
}