package goojeans.harulog.user.controller;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.domain.dto.ImageUrlString;
import goojeans.harulog.user.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/user/image")
    ResponseEntity<Response<ImageUrlString>> uploadUserImage(@RequestParam MultipartFile image) throws IOException {

        return ResponseEntity.ok(imageService.uploadUserImage(image));
    }
}
