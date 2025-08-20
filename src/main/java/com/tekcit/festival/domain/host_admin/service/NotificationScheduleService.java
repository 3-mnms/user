package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;

import java.util.List;

public interface NotificationScheduleService {
    NotificationScheduleResponseDTO create(NotificationScheduleDTO req, Long userId);
    NotificationScheduleResponseDTO update(Long id, NotificationScheduleDTO req, Long userId);
    void delete(Long id, Long userId);
    NotificationScheduleResponseDTO getById(Long id);
    List<NotificationScheduleResponseDTO> getByFestival(String fid);
    List<NotificationScheduleResponseDTO> getAll();
    List<NotificationScheduleResponseDTO> getByUserId(Long userId);
}