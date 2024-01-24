package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import jakarta.persistence.EntityNotFoundException;
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
    public ChatRoomDTO createChatRoom() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoomRepository.save(chatRoom);
        return ChatRoomDTO.of(chatRoom);
    }

    // 채팅방 조회
    @Override
    public ChatRoomDTO findByRoomId(Long roomId) {
        try{
            ChatRoom find = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found for id: " + roomId));
            return ChatRoomDTO.of(find);

        } catch (EntityNotFoundException e){
            log.error("Error finding ChatRoom Id: " + roomId);
            throw new BusinessException(ResponseCode.CHAT_NOT_FOUND);
        } catch (Exception e){
            log.error("findByRoomId() : {}", e.getMessage());
            throw e;
        }
    }

    // 채팅방 삭제 (soft delete)
    @Override
    public void deleteChatRoom(Long roomId) {
        try{
            ChatRoom find = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found for id: " + roomId));
            log.trace("find.id : {}", find.getId());
            chatRoomRepository.deleteById(roomId);

        } catch (EntityNotFoundException e){
            log.error("Error finding ChatRoom Id: " + roomId);
            throw new BusinessException(ResponseCode.CHAT_NOT_FOUND);
        } catch (Exception e){
            log.error("deleteChatRoom(): {}", e.getMessage());
            throw e;
        }
    }
}