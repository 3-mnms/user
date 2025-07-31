package com.tekcit.festival.domain.user.service;

// Mock 처리용 (나중에 삭제 예정)
public interface FcmSender {
    void send(String targetToken, String title, String body);
}