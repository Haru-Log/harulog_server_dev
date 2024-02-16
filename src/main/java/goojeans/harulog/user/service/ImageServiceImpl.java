package goojeans.harulog.user.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.domain.dto.ImageUrlString;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final FirebaseApp firebaseApp;

    @Override
    public Response<ImageUrlString> uploadUserImage(MultipartFile image) {

        Bucket bucket = StorageClient.getInstance(firebaseApp).bucket();
        JwtUserDetail currentUserInfo = securityUtils.getCurrentUserInfo();

        Users user = userRepository.findById(currentUserInfo.getId()).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        String blob = "image/profile/" + currentUserInfo.getId();
        InputStream streamImageFile;
        try {
            streamImageFile = new ByteArrayInputStream(image.getBytes());

            bucket.create(blob, streamImageFile, image.getContentType());

        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage());
            throw new BusinessException(ResponseCode.FIREBASE_ERROR);
        }

        user.updateImageUrl(blob);

        return Response.ok(new ImageUrlString(blob));
    }
}
