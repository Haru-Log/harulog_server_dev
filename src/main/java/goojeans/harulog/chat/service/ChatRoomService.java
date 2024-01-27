package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;

public interface ChatRoomService {

    // 채팅방 생성
    public ChatRoomDTO createChatRoom();

    // 채팅방 조회
    public ChatRoomDTO findByRoomId(Long roomId);

    // 채팅방 삭제 (soft delete)
    public void deleteChatRoom(Long roomId);

}