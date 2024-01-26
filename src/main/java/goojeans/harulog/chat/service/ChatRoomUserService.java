package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface ChatRoomUserService {

    // 채팅방에 유저 추가
    public Response<Void> create(Long roomId, Long userId);

    // 채팅방에 유저 삭제
    public Response<Void> delete(Long roomId, Long userId);

    // 채팅방에 참여하고 있는 유저 조회
    public Response<List<ChatUserDTO>> findByChatRoom(Long roomId);

    // 유저가 참여하고 있는 채팅방 조회
    public Response<List<ChatRoomDTO>> findByUser(Long userId);
}
