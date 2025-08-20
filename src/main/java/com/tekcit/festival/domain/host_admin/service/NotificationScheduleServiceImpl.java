package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.domain.host_admin.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import com.tekcit.festival.domain.host_admin.mapper.NotificationScheduleMapper;
import com.tekcit.festival.domain.host_admin.repository.NotificationScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduleServiceImpl implements NotificationScheduleService {

    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationScheduleMapper scheduleMapper;

    @Override
    public NotificationScheduleResponseDTO create(NotificationScheduleDTO req, Long userId) {
        String fid = req.getFid();
        LocalDateTime startAt = req.getStartAt();
        LocalDateTime sendTime = req.getSendTime();

        if (scheduleRepository.existsByFidAndStartAtAndSendTime(fid, startAt, sendTime)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        if (sendTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_SEND_TIME); // 유효하지 않은 발송 시각 에러코드
        }

        LocalDate day = sendTime.toLocalDate();
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long count = scheduleRepository.countByFidAndSendTimeBetween(fid, startOfDay, endOfDay);
        if (count >= 50) {
            throw new BusinessException(ErrorCode.REQUEST_LIMIT_EXCEEDED);
        }

        NotificationSchedule e = new NotificationSchedule();
        e.setFid(fid);
        e.setUserId(userId);
        e.setStartAt(startAt);
        e.setTitle(req.getTitle());
        e.setBody(req.getBody());
        e.setSendTime(sendTime);
        e.setSent(false);

        try {
            return scheduleMapper.toDto(scheduleRepository.save(e));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }
    }

    @Override
    public NotificationScheduleResponseDTO update(Long id, NotificationScheduleDTO req, Long userId) {
        NotificationSchedule e = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (e.isSent()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (!e.getUserId().equals(userId)) { // 소유권 확인
            throw new BusinessException(ErrorCode.FORBIDDEN_RESOURCE);
        }

        e.setFid(req.getFid());
        e.setStartAt(req.getStartAt());
        e.setTitle(req.getTitle());
        e.setBody(req.getBody());
        e.setSendTime(req.getSendTime());

        return scheduleMapper.toDto(scheduleRepository.save(e));
    }

    @Override
    public void delete(Long id, Long userId) {
        NotificationSchedule e = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (e.isSent()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (!e.getUserId().equals(userId)) { // 소유권 확인
            throw new BusinessException(ErrorCode.FORBIDDEN_RESOURCE);
        }

        scheduleRepository.delete(e);
    }

    @Override
    public NotificationScheduleResponseDTO getById(Long id) {
        NotificationSchedule e = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        return scheduleMapper.toDto(e);
    }

    @Override
    public List<NotificationScheduleResponseDTO> getByFestival(String fid) {
        // ✅ fid로 변경된 메서드 호출
        return scheduleMapper.toDtoList(
                scheduleRepository.findAllByFidOrderBySendTimeDesc(fid)
        );
    }

    @Override
    public List<NotificationScheduleResponseDTO> getAll() {
        return scheduleMapper.toDtoList(scheduleRepository.findAll());
    }

    @Override
    public List<NotificationScheduleResponseDTO> getByUserId(Long userId) {
        List<NotificationSchedule> userSchedules = scheduleRepository.findByUserId(userId);
        return scheduleMapper.toDtoList(userSchedules);
    }
}