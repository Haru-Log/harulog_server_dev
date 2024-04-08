package goojeans.harulog.comment.domain.entity;


import goojeans.harulog.comment.domain.dto.CommentRequestDto;
import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.post.domain.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE comment SET active_status = 'DELETED' WHERE comment_id = ? AND active_status <> 'DELETED'")
@SQLRestriction("active_status <> 'DELETED'")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @Column(nullable = false)
    private String nickname;        // 작성자명

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    @JoinColumn(name="parent_comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @Column(nullable = false)
    private int depth;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();


    @Column(length = 100, nullable = false)
    private String content;

    public Comment(CommentRequestDto requestDto, String nickname, Post post, Users user){
        this.content = requestDto.getContent();        // 댓글 내용
        this.nickname = nickname;                       // 작성자 닉네임
        this.user = user;                              // Users FK
        this.post = post;                               // Post FK
        this.depth = 0;                                 // 댓글 뎁스
    }

    public Comment(CommentRequestDto requestDto, String nickname, Post post, Users user, Comment comment){
        this.content = requestDto.getContent();        // 댓글 내용
        this.nickname = nickname;                       // 작성자 닉네임
        this.user = user;                              // Users FK
        this.post = post;                               // Post FK
        this.depth = 1;                                 // 댓글 뎁스
        this.parent = comment;
    }

    // 댓글 내용 업데이트 메소드
    public void update(CommentRequestDto requestDto){
        this.content = requestDto.getContent();
    }


}
