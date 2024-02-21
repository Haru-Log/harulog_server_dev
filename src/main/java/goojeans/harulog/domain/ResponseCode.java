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
    VALIDATION_FAIL(400, "COM-001", "형식이 맞지 않습니다.", HttpStatus.BAD_REQUEST),
    CONNECT_FAIL(500, "COM-002", "웹소켓 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILTER_FAIL(500, "COM-101", "필터에서 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 유저 : USR
    USER_NOT_FOUND(400, "USR-001", "유저를 찾을 수 없습니다.",HttpStatus.BAD_REQUEST),
    USER_LOGIN_FAIL(400, "USR-002", "로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
    USER_OAUTH_LOGIN_FAIL(400, "USR-003", "소셜 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
    USER_NICKNAME_DUPLICATION(400, "USR-004", "닉네임이 중복되었습니다.", HttpStatus.BAD_REQUEST),
    USER_EMAIL_DUPLICATION(400, "USR-005", "이메일이 중복되었습니다.", HttpStatus.BAD_REQUEST),
    USER_NICKNAME_EMAIL_DUPLICATION(400, "USR-006", "닉네임과 이메일이 중복되었습니다.", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_NOT_MATCH(400, "USR-007", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_DELETE_STRING_NOT_MATCH(400, "USR-008", "이메일이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_UNAUTHORIZED(401, "USR-011", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    USER_LOGIN_REQUIRED(401, "USR-012", "다시 로그인해주세요.", HttpStatus.UNAUTHORIZED),

    // 유저 목표 : USG
    USER_GOAL_ALREADY_EXIST(400, "USG-001", "이미 존재하는 목표 입니다.",HttpStatus.BAD_REQUEST),
    USER_GOAL_INVALID_DATA(400, "USG-002", "입력 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_GOAL_CATEGORY_NOT_FOUND(400, "USG-003", "해당하는 카테고리를 찾지 못했습니다.",HttpStatus.BAD_REQUEST),
    USER_GOAL_NOT_FOUND(400, "USG-004", "목표들을 찾지 못했습니다.", HttpStatus.BAD_REQUEST),
    USER_GOAL_UPDATE_FAIL(500, "USG-101", "업데이트에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    // 팔로우 : FLW
    // 챌린지 : CHL
    // 카테고리 : CAT
    // 게시글 : POS
    POS_NOT_FOUND(400,"POS_001","피드를 찾을 수 없습니다. ",HttpStatus.BAD_REQUEST),
    POS_CATEGORY_NOT_FOUND(400, "POS-002", "해당하는 카테고리를 찾지 못했습니다.", HttpStatus.BAD_REQUEST),
    POS_AUTHENTICATION_FAIL(400,"POS-003", "유저 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    POS_UPDATE_TIME_FAIL(400,"POS-004", "수정할 수 있는 시간이 지났습니다.",HttpStatus.BAD_REQUEST),

    // 댓글 : CMT
    CMT_AUTHENTICATION_FAIL(400, " CMT-101", "유저 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CMT_NOT_FOUND(400,"CMT_201", "댓글을 찾을 수 없습니다.",HttpStatus.BAD_REQUEST),
    CMT_PARENT_NOT_FOUND(400, "CMT-202", "부모 댓글을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CMT_POST_NOT_FOUND(400, "CMT-203", "게시물을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    // 좋아요 : LIK
    Lik_AUTHENTICATION_FAIL(400,"LIK-001", "유저 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),

    // 채팅 : CHT
    CHAT_TRANSMISSION_FAIL(400, "CHT-001", "채팅 전송에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CHAT_AUTHENTICATION_FAIL(400, "CHT-101", "유저 인증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CHAT_NO_PERMISSION(400, "CHT-102", "채팅방에 참여하지 않은 사용자입니다.", HttpStatus.BAD_REQUEST),
    CHATROOM_NOT_FOUND(400, "CHT-201", "채팅방을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHATROOM_USER_NOT_ENOUGH(400, "CHT-202", "채팅방에 참여할 유저가 부족합니다.", HttpStatus.BAD_REQUEST),
    CHALLENGE_CHATROOM_USER_CANNOT_LEAVE(400, "CHT-203", "챌린지에서 먼저 나가야 합니다.", HttpStatus.BAD_REQUEST),
    NO_MORE_MESSAGE(200, "CHT-301", "더 이상 메세지가 없습니다.", HttpStatus.OK),

    //챌린지 : CHL
    CHALLENGE_NOT_FOUND(400, "CHL-001", "챌린지를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHALLENGE_ALREADY_JOIN(400, "CHL-002", "이미 해당 챌린지에 참여 중입니다.", HttpStatus.BAD_REQUEST),
    CHALLENGE_NO_PERMISSION(400, "CHL-003", "챌린지에 참여하지 않은 사용자입니다.", HttpStatus.BAD_REQUEST),
    CHALLENGE_UNAUTHORIZED_ACCESS(400, "CHL-004", "권한이 없습니다.", HttpStatus.BAD_REQUEST),
    CHALLENGE_CAT_ALREADY_PARTICIPATE(400, "CHL-005", "이미 해당 카테고리의 챌린지에 참여 중입니다.", HttpStatus.BAD_REQUEST),
    CHALLENGE_CANNOT_KICKOUT_SELF(400, "CHL-006","자신을 강퇴할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHALLENGE_LEADER_CANNOT_LEAVE(400, "CHL-007", "챌린지 리더는 챌린지를 떠날 수 없습니다.", HttpStatus.BAD_REQUEST),

    //카테고리 : CAT
    CATEGORY_NOT_FOUND(400, "CAT-001", "카테고리를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    //구글 파이어베이스: GFB
    FIREBASE_ERROR(500, "GFB-001", "이미지 업로드 중 문제가 생겼습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    //어드민 : ADM
    ADMIN_CANNOT_DELETE(400, "ADM-001", "계정을 삭제할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ONLY_ADMIN_CAN_ACCESS(401, "ADM-011", "권한이 없습니다.", HttpStatus.UNAUTHORIZED);

    final Integer status;
    final String code;
    final String message;
    final HttpStatus httpStatus;

}
