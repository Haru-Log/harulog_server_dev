package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomUserDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;

import java.util.List;

public interface ChatRoomUserService {

    // 채팅방에 유저 추가
    public ChatRoomUserDTO addUser(Long roomId, Long userId);

    // 채팅방에 유저 삭제
    public void deleteUser(Long roomId, Long userId);

    // 채팅방에 참여하고 있는 유저 조회
    public List<ChatRoomUserDTO> findByChatRoom(Long roomId);

    // 유저가 참여하고 있는 채팅방 조회
    public List<ChatRoomUserDTO> findByUser(Long userId);
}
