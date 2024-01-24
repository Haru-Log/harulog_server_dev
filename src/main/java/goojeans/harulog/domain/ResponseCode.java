package goojeans.harulog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * status: 원하는 status code 를 기입합니다. ex) 200, 400
 * code: 커스텀 코드
 * message: 메세지
 * httpStatus: ResponseEntity 에서 사용할 HttpsStatus 를 입력합니다. ex) 200번이면 HttpStatus.OK
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    /**
     * Guide: User 엔티티의 경우 USER_BAD_REQUEST, Chat 엔티티의 경우 CHAT_BAD_REQUEST 형식으로 엔티티 이름을 앞에다 붙여줄 것.
     */
    SUCCESS(200, "COM-000", "OK", HttpStatus.OK),

    // 유저 : USR
    // 유저 목표 : USG
    // 팔로우 : FLW
    // 챌린지 : CHL
    // 카테고리 : CAT
    // 게시글 : POS
    // 댓글 : CMT
    // 좋아요 : LIK

    // 채팅 : CHT
    CHAT_TRANSMISSION_FAIL(400, "CHT-001", "채팅 전송에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CHAT_AUTEHNTICATION_FAIL(400, "CHT-101", "유저 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CHAT_NO_PERMISSION(400, "CHT-102", "채팅방에 참여하지 않은 사용자입니다.", HttpStatus.BAD_REQUEST),
    CHAT_NOT_FOUND(400, "CHT-201", "채팅방을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);


    final Integer status;
    final String code;
    final String message;
    final HttpStatus httpStatus;

}
