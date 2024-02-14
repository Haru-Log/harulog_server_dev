package goojeans.harulog.user.service;

import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.response.ImageUrlString;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FirebaseApp firebaseApp;

    @Test
    @DisplayName("유저 프사 업로드")
    void uploadUserImage() {
        //Given
        MockedStatic<StorageClient> mockedStorageClient = mockStatic(StorageClient.class);
        Bucket bucket = mock(Bucket.class);
        StorageClient storageClient = mock(StorageClient.class);

        String testString = "test";
        Long testId = 1L;
        String blob = "image/profile/" + testId;

        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId)
                .password(testString)
                .username(testString)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                .build();

        Users user = Users.builder()
                .id(testId)
                .build();

        MockMultipartFile mockFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "some bytes".getBytes());

        mockedStorageClient.when(() -> StorageClient.getInstance(firebaseApp)).thenReturn(storageClient);
        when(storageClient.bucket()).thenReturn(bucket);

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
        doReturn(null).when(bucket).create(eq(blob), any(ByteArrayInputStream.class), eq("image/jpeg"));
        doReturn(Optional.of(user)).when(userRepository).findById(testId);

        //When
        Response<ImageUrlString> result = imageService.uploadUserImage(mockFile);

        //Then
        assertThat(result.getData().getImageUrl()).isEqualTo(blob);
        verify(securityUtils, times(1)).getCurrentUserInfo();
        verify(userRepository, times(1)).findById(testId);
        verify(bucket, times(1)).create(eq(blob), any(InputStream.class), eq("image/jpeg"));

    }

}
