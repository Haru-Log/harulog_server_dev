package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface ChatRoomService {

    // 채팅방 생성
    Response<ChatRoomDTO> createChatRoom(List<String> nicknames);

    // 챌린지 채팅방 생성
    ChatRoom createChallengeChatRoom(String challengeName, String imageUrl);

    // 채팅방 조회
    Response<ChatRoomDTO> findByRoomId(String roomId);

    // 채팅방 삭제 (soft delete)
    Response<Void> deleteChatRoom(String roomId);

}