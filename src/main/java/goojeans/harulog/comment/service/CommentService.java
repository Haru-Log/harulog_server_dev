package goojeans.harulog.comment.service;

import goojeans.harulog.comment.domain.dto.CommentRequestDto;
import goojeans.harulog.comment.domain.dto.CommentResponseDto;
import goojeans.harulog.comment.domain.entity.Comment;
import goojeans.harulog.comment.repository.CommentRepository;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.post.repository.PostRepository;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;



    public CommentResponseDto createComment(Long postId, Long commentId, CommentRequestDto requestDto, Long userId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ResponseCode.CMT_POST_NOT_FOUND));

        Users user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL));

        Comment comment;
        if (commentId == 0) {
            comment = commentRepository.save(new Comment(requestDto, user.getNickname(), post, user));
        } else {
            Comment childComment = commentRepository.findById(commentId).orElseThrow(
                    () -> new BusinessException(ResponseCode.CMT_PARENT_NOT_FOUND));

            if (commentRepository.findByPostAndId(post, commentId).isEmpty()) {
                throw new BusinessException(ResponseCode.CMT_PARENT_NOT_FOUND);
            }

            comment = commentRepository.save(new Comment(requestDto, user.getNickname(), post, user, childComment));
        }
        return new CommentResponseDto(comment, commentId);
    }


    public CommentResponseDto getComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_POST_NOT_FOUND)
        );

        List<Comment> childrenComments = commentRepository.findChildrenComments(id);
        List<CommentResponseDto> childrenDtoList = new ArrayList<>();

        for (Comment childComment : childrenComments) {
            CommentResponseDto childDto = new CommentResponseDto(childComment, id);
            childrenDtoList.add(childDto);
        }

        return  new CommentResponseDto(comment, childrenDtoList);
    }



    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto, Long userId) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_NOT_FOUND)
        );

        if(comment.getUser().getId().equals(userId)){
            comment.update(requestDto);
        }else{
            throw new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL);
        }
        return new CommentResponseDto(comment);
    }


    public Response<Void> deleteComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_NOT_FOUND)
        );
        if(comment.getUser().getId().equals(userId)){
            commentRepository.deleteById(id);
        }else {
            throw new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL);
        }

        return Response.ok();
    }


}


