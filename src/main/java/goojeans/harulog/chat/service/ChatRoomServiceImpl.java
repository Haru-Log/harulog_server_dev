package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
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

    // 채팅방 생성
    @Override
    public Response<ChatRoomDTO> createDM() {
        log.trace("create DM execute");

        ChatRoom chatRoom = ChatRoom.createDM();
        chatRoomRepository.save(chatRoom);
        return Response.ok(ChatRoomDTO.of(chatRoom));
    }

    @Override
    public Response<ChatRoomDTO> createChallenge(String name, String imageUrl) {
        log.trace("create ChallengeChatRoom execute");

        ChatRoom chatRoom = ChatRoom.createChallenge(name, imageUrl);
        chatRoomRepository.save(chatRoom);
        return Response.ok(ChatRoomDTO.of(chatRoom));
    }

    // 채팅방 조회
    @Override
    public Response<ChatRoomDTO> findByRoomId(String roomId) {
        log.trace("findByRoomId() execute");

        ChatRoom find = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NOT_FOUND));
        return Response.ok(ChatRoomDTO.of(find));
    }

    // 채팅방 삭제 (soft delete)
    @Override
    public Response<Void> delete(String roomId) {
        log.trace("deleteChatRoom() execute");

        chatRoomRepository.deleteById(roomId);
        return Response.ok();
    }
}