package goojeans.harulog.post.controller;



import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.post.domain.dto.PostRequestDto;
import goojeans.harulog.post.domain.dto.PostResponseDto;
import goojeans.harulog.post.service.PostService;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;
    private final SecurityUtils securityUtils;

    @PostMapping("/feed/create")
    public ResponseEntity<Response<PostResponseDto>> addPost(@Validated @RequestBody PostRequestDto requestDto) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        PostResponseDto postResponseDto = postService.createPost(requestDto, userId);
        // 성공 응답 생성
        return ResponseEntity.ok(Response.ok(postResponseDto));
    }


    //게시글 상세 보기
    @GetMapping("/feed")
    public ResponseEntity<Response<PostResponseDto>> getPost(@RequestParam Long postId){
        return ResponseEntity.ok(Response.ok(postService.getPost(postId)));
    }

    //게시글 좋아요 순으로 정렬
    @GetMapping("/feed/like")
    public ResponseEntity<Response<List<PostResponseDto>>> getPostOrderByLikes(){
        List<PostResponseDto> Posts = postService.getPostsOrderByLikes();
        return ResponseEntity.ok(Response.ok(Posts));
    }

    //카테고리 별 좋아요 순으로 정렬
    @GetMapping("/feed/like/category")
    public ResponseEntity<Response<List<PostResponseDto>>> getPostCategoryOrderByLikes(@RequestBody PostRequestDto requestDto){
        String categoryName = requestDto.getCategoryName();
        List<PostResponseDto> Posts = postService.getPostCategoryOrderByLikes(categoryName);
        return ResponseEntity.ok(Response.ok(Posts));

    }


    //유저의 게시글 전체 조회
    @GetMapping("/feed/user")
    public ResponseEntity<Response<List<PostResponseDto>>> getUserPosts() {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        List<PostResponseDto> userPosts = postService.getUserPost(userId);
        return ResponseEntity.ok(Response.ok(userPosts));
    }


    @PutMapping("/feed/{post_id}")
    public ResponseEntity<Response<PostResponseDto>> updatePost(@Validated @PathVariable Long post_id,
                                                      @RequestBody PostRequestDto requestDto){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(Response.ok(postService.updatePost(post_id, requestDto, userId)));
    }

    @DeleteMapping("/feed/{post_id}")
    public ResponseEntity<Response<PostResponseDto>> deletePost(@PathVariable Long post_id) {
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(Response.ok(postService.deletePost(post_id, userId)));
    }

    //좋아요 누른 Feed 보기
    @GetMapping("/feed/like/user")
    public ResponseEntity<Response<List<PostResponseDto>>> userLikePost(){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(Response.ok(postService.userLikePost(userId)));
    }

    //팔로우 중인 사람들의 feed 보기
    @GetMapping("/feed/follow/user")
    public ResponseEntity<Response<List<PostResponseDto>>> userFollowPost(){
        Long userId = securityUtils.getCurrentUserInfo().getId();
        return ResponseEntity.ok(Response.ok(postService.userFollowPost(userId)));
    }

    //게시글 전체 보기
    @GetMapping("/feed/all")
    public ResponseEntity<Response<List<PostResponseDto>>> getAllPost(){
        return ResponseEntity.ok(Response.ok(postService.getAllPost()));
    }

}
