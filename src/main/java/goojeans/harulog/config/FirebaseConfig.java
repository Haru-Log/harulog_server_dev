package goojeans.harulog.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(@Value("${firebase.key}")String envString,
                                   @Value("${firebase.bucket}") String bucket) throws IOException {

        String auth = new String(Base64.getDecoder().decode(envString));

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(auth.getBytes());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(byteArrayInputStream))
                .setStorageBucket(bucket)
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);

        return app;

    }

}
