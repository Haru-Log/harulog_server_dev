package goojeans.harulog.likes.service;

import goojeans.harulog.likes.repository.LikesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {

    private static final Logger logger = LoggerFactory.getLogger(LikesService.class);

    private final LikesRepository likesRepository;



    public void saveLikes(Long postId, Long userId) {
        try {
            if (userId == null) {
                logger.warn("userId is null for postId: {}", postId);
                return;
            }
            likesRepository.insertLikes(postId, userId);
        } catch (Exception e) {
            logger.error("Error occurred while saving likes for postId: {}", postId, e);
            throw new RuntimeException("좋아요 저장 중 오류 발생", e);
        }
    }


    public void deleteLikes(Long postId, Long userId) {
        try {
            if (userId == null) {
                logger.warn("userId is null for postId: {}", postId);
                return;
            }
            likesRepository.unLike(postId, userId);
        } catch (Exception e) {
            logger.error("Error occurred while deleting likes for postId: {}", postId, e);
            throw new RuntimeException("좋아요 삭제 중 오류 발생", e);
        }
    }
}
