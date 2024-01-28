package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.domain.dto.Response;

public interface ChatRoomService {

    // 채팅방 생성
    public Response<ChatRoomDTO> create();

    public Response<ChatRoomDTO> create(String name);

    // 채팅방 조회
    public Response<ChatRoomDTO> findByRoomId(String roomId);

    // 채팅방 삭제 (soft delete)
    public Response<Void> delete(String roomId);

}