package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.repository.ChatRoomRepository;
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
            throw e; // todo : custom exception (일단 GlobalExceptionHandler에서 404 처리)
        } catch (Exception e){
            log.error("findByRoomId Error : {}", e.getMessage());
            throw e;
        }
    }

    // 채팅방 삭제 (soft delete)
    @Override
    public void deleteChatRoom(Long roomId) {
        try{
            ChatRoom find = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found for id: " + roomId));
            chatRoomRepository.deleteById(roomId);

        } catch (EntityNotFoundException e){
            log.error("Error finding ChatRoom Id: " + roomId);
            throw e; // todo : custom exception (일단 GlobalExceptionHandler에서 404 처리)
        } catch (Exception e){
            log.error("deleteChatRoom Error : {}", e.getMessage());
            throw e;
        }
    }
}