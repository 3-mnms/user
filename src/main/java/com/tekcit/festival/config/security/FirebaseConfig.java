package com.tekcit.festival.config.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.key-path}")
    private String firebaseKeyPath;

    @PostConstruct
    public void initFirebase() throws IOException {
        InputStream serviceAccount;

        if (firebaseKeyPath.startsWith("classpath:")) {
            String resourcePath = firebaseKeyPath.replace("classpath:", "");
            serviceAccount = new ClassPathResource(resourcePath).getInputStream();
        } else {
            serviceAccount = new FileInputStream(firebaseKeyPath);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("[Firebase] 초기화 완료");
        }
    }
}