package goojeans.harulog.chat.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.domain.dto.ImageUrlString;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatImageServiceImpl implements ChatImageService {

    private final FirebaseApp firebaseApp;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;

    @Override
    public Response<ImageUrlString> uploadChatImage(MultipartFile image, String roomId) {

        // firebase에 접근하기 위한 bucket 객체 생성
        Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();

        Long userId = securityUtils.getCurrentUserInfo().getId();
        validateUser(userId);

        // "image/chat/{roomId}/{timestamp}-{userId}"
        String blob = "image/chat/" + roomId + "/" + LocalDateTime.now() + "-" + userId;

        InputStream stream;
        try {
            // 이미지 데이터를 메모리에 로드
            stream = new ByteArrayInputStream(image.getBytes());

            // firebase storage에 업로드
            // (저장경로, 업로드할 데이터, 파일 MIME 타입)
            bucket.create(blob, stream, image.getContentType());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ResponseCode.FIREBASE_ERROR);
        }

        return Response.ok(new ImageUrlString(blob));
    }

    private void validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }
}
