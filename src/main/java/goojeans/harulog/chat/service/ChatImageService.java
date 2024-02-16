package goojeans.harulog.chat.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.response.ImageUrlString;
import org.springframework.web.multipart.MultipartFile;

public interface ChatImageService {
    Response<ImageUrlString> uploadChatImage(MultipartFile image, String roomId);
}
