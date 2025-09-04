package com.tekcit.festival.domain.host_admin.service;

import com.tekcit.festival.domain.host_admin.dto.request.NotificationUpdateScheduleDTO;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import com.tekcit.festival.domain.host_admin.dto.request.NotificationScheduleDTO;
import com.tekcit.festival.domain.host_admin.dto.response.NotificationScheduleResponseDTO;
import com.tekcit.festival.domain.host_admin.entity.NotificationSchedule;
import com.tekcit.festival.domain.mapper.NotificationScheduleMapper;
import com.tekcit.festival.domain.host_admin.repository.NotificationScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationScheduleServiceImpl implements NotificationScheduleService {

    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public NotificationScheduleResponseDTO create(NotificationScheduleDTO req, Long userId) {
        String fid = req.getFid();
        LocalDateTime startAt = req.getStartAt();
        LocalDateTime sendTime = req.getSendTime();

        // 1. 중복 알림 예약 검증
        if (scheduleRepository.existsByFidAndStartAtAndSendTime(fid, startAt, sendTime)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE);
        }

        // 2. 과거 시점 발송 검증
        if (sendTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_SEND_TIME);
        }

        // 3. 일일 알림 예약 횟수 제한 검증 (최대 50건)
        LocalDate day = sendTime.toLocalDate();
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long count = scheduleRepository.countByFidAndSendTimeBetween(fid, startOfDay, endOfDay);
        if (count >= 50) {
            throw new BusinessException(ErrorCode.REQUEST_LIMIT_EXCEEDED);
        }

        // 4. 엔티티 생성 및 저장
        NotificationSchedule e = NotificationSchedule.builder()
                .fid(fid)
                .userId(userId)
                .startAt(startAt)
                .title(req.getTitle())
                .body(req.getBody())
                .sendTime(sendTime)
                .isSent(false)
                .fname(req.getFname())
                .build();

        return scheduleMapper.toDto(scheduleRepository.save(e));
    }

    @Override
    @Transactional
    public NotificationScheduleResponseDTO update(Long id, NotificationUpdateScheduleDTO req, Long userId) {
        NotificationSchedule e = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 1. 이미 발송된 알림인지 검증
        if (e.isSent()) {
            throw new BusinessException(ErrorCode.ALREADY_SENT_NOTIFICATION);
        }
        // 2. 소유권 검증
        if (!e.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_RESOURCE);
        }

        // 3. 업데이트 필드 적용
        if (req.getTitle() != null)    e.setTitle(req.getTitle());
        if (req.getBody() != null)     e.setBody(req.getBody());
        if (req.getSendTime() != null) {
            // 발송 시각 변경 시, 과거 시점인지 재검증
            if (req.getSendTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.INVALID_SEND_TIME);
            }
            e.setSendTime(req.getSendTime());
        }

        return scheduleMapper.toDto(e); // 트랜잭션이 종료될 때 변경사항이 자동 저장됨
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        NotificationSchedule e = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 1. 이미 발송된 알림인지 검증
        if (e.isSent()) {
            throw new BusinessException(ErrorCode.ALREADY_SENT_NOTIFICATION);
        }
        // 2. 소유권 검증
        if (!e.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_RESOURCE);
        }

        scheduleRepository.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationScheduleResponseDTO getById(Long id) {
        NotificationSchedule e = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        return scheduleMapper.toDto(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationScheduleResponseDTO> getByFestival(String fid) {
        return scheduleMapper.toDtoList(
                scheduleRepository.findAllByFidOrderBySendTimeDesc(fid)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationScheduleResponseDTO> getAll() {
        return scheduleMapper.toDtoList(scheduleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationScheduleResponseDTO> getByUserId(Long userId) {
        List<NotificationSchedule> userSchedules = scheduleRepository.findByUserId(userId);
        return scheduleMapper.toDtoList(userSchedules);
    }
}