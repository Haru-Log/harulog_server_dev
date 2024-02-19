package goojeans.harulog.likes.service;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.likes.domain.dto.LikesResponseDto;
import goojeans.harulog.likes.repository.LikesRepository;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LikesService {


    private final LikesRepository likesRepository;
    private final UserRepository userRepository;




    public LikesResponseDto saveLikes(Long postId, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.Lik_AUTHENTICATION_FAIL));
        likesRepository.insertLikes(postId, userId);
        int updatedLikeCount = likesRepository.countLikesByPostId(postId);

        return new LikesResponseDto(updatedLikeCount);
    }


    public LikesResponseDto deleteLikes(Long postId, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.Lik_AUTHENTICATION_FAIL));
        likesRepository.unLike(postId, userId);
        int updatedLikeCount = likesRepository.countLikesByPostId(postId);

        return new LikesResponseDto(updatedLikeCount);

    }
}
