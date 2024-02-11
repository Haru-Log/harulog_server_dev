package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.config.RabbitMQConfig;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RabbitMQConfig rabbitMQConfig;

    /**
     * 채팅방 생성
     * + 채팅방 생성 시 exchange 생성
     * todo: 채팅방 생성(정적 팩터리 메서드) -> ChatRoom.createRoom(DM) 이런식으로 리팩터링.
     */
    @Override
    public Response<ChatRoomDTO> createChatRoom() {
        log.trace("create ChatRoom");

        ChatRoom chatRoom = ChatRoom.createDM();
        chatRoomRepository.save(chatRoom);

        // 채팅방 생성 시 exchange 생성
        rabbitMQConfig.createFanoutExchange(chatRoom.getId());

        return Response.ok(ChatRoomDTO.of(chatRoom));
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

        chatRoomRepository.deleteById(roomId);
        return Response.ok();
    }
}