package goojeans.harulog.user.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.response.ImageUrlString;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Response<ImageUrlString> uploadUserImage(MultipartFile image);
}
