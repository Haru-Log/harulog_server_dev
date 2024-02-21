package goojeans.harulog.post.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.category.repository.CategoryRepository;
import goojeans.harulog.comment.domain.dto.CommentResponseDto;
import goojeans.harulog.comment.domain.entity.Comment;
import goojeans.harulog.comment.repository.CommentRepository;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.ImageUrlString;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.likes.repository.LikesRepository;
import goojeans.harulog.post.domain.dto.PostLikeResponseDto;
import goojeans.harulog.post.domain.dto.PostRequestDto;
import goojeans.harulog.post.domain.dto.PostResponseDto;
import goojeans.harulog.post.domain.entity.Post;

import goojeans.harulog.post.repository.PostRepository;
import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserGoalRepository;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final UserGoalRepository userGoalRepository;
    private final CategoryRepository categoryRepository;
    private final FirebaseApp firebaseApp;


    public PostResponseDto createPost(PostRequestDto postRequestDto, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL));

        // categoryName을 이용하여 categoryId 가져오기
        Category category = categoryRepository.findByCategoryName(postRequestDto.getCategoryName())
                .orElseThrow(() -> new BusinessException(ResponseCode.POS_CATEGORY_NOT_FOUND));

        // 사용자의 ID 및 카테고리에 따른 목표 가져오기
        int userGoal = getUserGoalByUserIdAndCategory(userId, category);

        // Post 객체 생성
        Post post = postRepository.save(new Post(postRequestDto, user, category, userGoal));

        // PostResponseDto 생성
        return new PostResponseDto(post);
    }

    private int getUserGoalByUserIdAndCategory(Long userId, Category category) {
        return userGoalRepository.findUserGoalByUserIdAndCategory(userId, category)
                .map(UserGoal::getGoal)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_GOAL_INVALID_DATA));
    }

    //feed 이미지 추가
    public Response<ImageUrlString> postImage(Long userId, Long feedId, MultipartFile image) {

        Post post = postRepository.findById(feedId).orElseThrow(
                () -> new BusinessException(ResponseCode.POS_NOT_FOUND)
        );

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ResponseCode.POS_AUTHENTICATION_FAIL);
        }

        Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();


        String blob = "image/feed/" + feedId;
        InputStream streamImageFile;
        try {
            streamImageFile = new ByteArrayInputStream(image.getBytes());

            bucket.create(blob, streamImageFile, image.getContentType());
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage());
            throw new BusinessException(ResponseCode.FIREBASE_ERROR);
        }
        post.updateImage(blob);

        return Response.ok(new ImageUrlString(blob));
    }



    // 하나의 Post 상세

    public PostResponseDto getPost(Long id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new BusinessException(ResponseCode.POS_NOT_FOUND)
        );

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        int likeCount = likesRepository.countLikesByPostId(post.getId());
        int commentCount = commentRepository.countCommentsByPostId(post.getId());
        for (Comment comment : post.getComments()) {
            List<CommentResponseDto> childCommentList = new ArrayList<>();
            if(comment.getParent()==null){                                                      //부모 댓글이 없을 경우
                for (Comment childComment : comment.getChildren()){                              //자식 댓글 리스트의 데이터를 childComment에 저장
                    if (id.equals(childComment.getPost().getId())) {                         //childComment의 id와 받아온 id가 일치할 경우(선택 게시글 저장)
                        childCommentList.add(new CommentResponseDto(childComment));             //저장된 자식댓글을 리스트에 저장
                    }
                }
                commentResponseDtoList.add(new CommentResponseDto(comment,childCommentList));   //저장된 데이터를 리스트에
            }
        }


        return new PostResponseDto(post, commentResponseDtoList,commentCount, likeCount);
    }

    //하나의 피드 상세 유저가 좋아요 눌렀는지 여부도 포함
    public PostLikeResponseDto getPostUser(Long postId, Long userId){
        Users users = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_POST_NOT_FOUND)
        );

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ResponseCode.CMT_POST_NOT_FOUND)
        );

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        int likeCount = likesRepository.countLikesByPostId(post.getId());
        int commentCount = commentRepository.countCommentsByPostId(post.getId());
        boolean likedByCurrentUser = likesRepository.existsByPostIdAndUserId(postId, userId);
        for (Comment comment : post.getComments()) {
            List<CommentResponseDto> childCommentList = new ArrayList<>();
            if (comment.getParent() == null) {
                for (Comment childComment : comment.getChildren()) {
                    if (postId.equals(childComment.getPost().getId())) {
                        childCommentList.add(new CommentResponseDto(childComment));
                    }
                }
                commentResponseDtoList.add(new CommentResponseDto(comment, childCommentList));   //저장된 데이터를 리스트에
            }
        }

        return new PostLikeResponseDto(post, commentResponseDtoList, commentCount, likeCount,likedByCurrentUser);


    }

    //유저의 게시글 전체 조회

    public List<PostResponseDto> getUserPost(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.POS_AUTHENTICATION_FAIL));

        List<Post> postList = postRepository.findByUserId(id);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = likesRepository.countLikesByPostId(post.getId());
            int commentCount = commentRepository.countCommentsByPostId(post.getId());

            postResponseDtoList.add(new PostResponseDto(post,commentCount, likeCount));
        }

        return postResponseDtoList;
    }

    //좋아요 높은순서대로 피드 조회

    public List<PostResponseDto> getPostsOrderByLikes() {
        // 좋아요가 높은 순서대로 게시물 가져오기
        List<Post> postList = postRepository.findPostsOrderByLikes();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = likesRepository.countLikesByPostId(post.getId());
            int commentCount = commentRepository.countCommentsByPostId(post.getId());
            postResponseDtoList.add(new PostResponseDto(post, commentCount, likeCount));
        }

        return postResponseDtoList;
    }

    //카테고리로 좋아요 높은순으로 피드 조회

    public List<PostResponseDto> getPostCategoryOrderByLikes(String categoryName){
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new BusinessException(ResponseCode.POS_CATEGORY_NOT_FOUND));
        List<Post> postList = postRepository.findPostsByCategoryOrderByLikes(category.getCategoryId());

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Post post : postList) {
            int likeCount = likesRepository.countLikesByPostId(post.getId());
            int commentCount = commentRepository.countCommentsByPostId(post.getId());


            postResponseDtoList.add(new PostResponseDto(post, commentCount, likeCount));
        }
        return postResponseDtoList;
    }








    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ResponseCode.POS_NOT_FOUND)
        );

        LocalDate postCreationDate = post.getCreatedAt().toLocalDate();
        LocalDate currentDate = LocalDate.now();

        if (userId.equals(post.getUser().getId())) {
            if(!currentDate.isEqual(postCreationDate)) {
                throw new BusinessException(ResponseCode.POS_UPDATE_TIME_FAIL);
            }else {
                post.update(postRequestDto);
                return new PostResponseDto(post);
            }
        } else {
            throw new BusinessException(ResponseCode.POS_AUTHENTICATION_FAIL);
        }
    }



    public Response<Void> deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new BusinessException(ResponseCode.POS_NOT_FOUND)
        );

        if (post.getUser().getId().equals(userId)) {
            postRepository.deleteById(postId);
        } else {
            throw new BusinessException(ResponseCode.POS_AUTHENTICATION_FAIL);
        }
        return Response.ok();
    }

    public List<PostResponseDto> userLikePost(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.POS_AUTHENTICATION_FAIL));

        List<Post> postList = postRepository.findPostsByUserOrderByCreatedAtDesc(userId);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Post post : postList) {
            int likeCount = likesRepository.countLikesByPostId(post.getId());
            int commentCount = commentRepository.countCommentsByPostId(post.getId());
            postResponseDtoList.add(new PostResponseDto(post, commentCount, likeCount));
        }

        return postResponseDtoList;
    }

    public List<PostResponseDto> userFollowPost(Long userId){
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CMT_AUTHENTICATION_FAIL));
        List<Post> postList = postRepository.findPostsByFollowersOrderByCreatedAtDesc(userId);
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for(Post post : postList){
            int likeCount = likesRepository.countLikesByPostId(post.getId());
            int commentCount = commentRepository.countCommentsByPostId(post.getId());
            postResponseDtoList.add(new PostResponseDto(post, commentCount, likeCount));
        }
        return postResponseDtoList;
    }

    public List<PostResponseDto> getAllPost(){
        List<Post> postList = postRepository.findAll();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for(Post post : postList){
            int likeCount = likesRepository.countLikesByPostId(post.getId());
            int commentCount = commentRepository.countCommentsByPostId(post.getId());
            postResponseDtoList.add(new PostResponseDto(post, commentCount, likeCount));
        }
        return postResponseDtoList;
    }



}
