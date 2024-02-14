package goojeans.harulog.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(@Value("${firebase.key}")String auth) throws IOException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(auth.getBytes());

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(byteArrayInputStream))
                .setStorageBucket("haru-log-test.appspot.com")
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);

        return app;

    }

}
