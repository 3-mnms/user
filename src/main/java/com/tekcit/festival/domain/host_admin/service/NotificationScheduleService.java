package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.host_admin.dto.request.NotificationUpdateScheduleDTO;

import java.util.List;

public interface NotificationScheduleService {
    NotificationScheduleResponseDTO create(NotificationScheduleDTO req, Long userId);
    NotificationScheduleResponseDTO update(Long id, NotificationUpdateScheduleDTO req, Long userId);
    void delete(Long id, Long userId);
    NotificationScheduleResponseDTO getById(Long id);
    List<NotificationScheduleResponseDTO> getByFestival(String fid);
    List<NotificationScheduleResponseDTO> getAll();
    List<NotificationScheduleResponseDTO> getByUserId(Long userId);
}