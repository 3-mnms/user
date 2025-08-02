
public interface NotificationScheduleService {
    NotificationScheduleResponseDTO createSchedule(NotificationScheduleDTO request);
    NotificationScheduleResponseDTO updateSchedule(Long id, NotificationScheduleDTO request);
    void deleteSchedule(Long id);
    List<NotificationScheduleSummaryDTO> getSchedulesByFestival(Long festivalId);
    List<NotificationScheduleSummaryDTO> getAllSchedules();
}

@Service
@RequiredArgsConstructor
class NotificationScheduleServiceImpl implements NotificationScheduleService {
    private final FestivalRepository festivalRepository;
    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationScheduleMapper scheduleMapper;

    @Override
    public NotificationScheduleResponseDTO createSchedule(NotificationScheduleDTO request) {
        Long internalFestivalId = Long.parseLong(request.getFestivalId());
        Festival festival = festivalRepository.findByFestivalId(internalFestivalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FESTIVAL_NOT_FOUND));

        NotificationSchedule schedule = NotificationSchedule.builder()
                .festival(festival)
                .title(request.getTitle())
                .body(request.getBody())
                .sendTime(request.getSendTime())
                .isSent(false)
                .build();

        return scheduleMapper.toDto(scheduleRepository.save(schedule));
    }

    @Override
    public NotificationScheduleResponseDTO updateSchedule(Long id, NotificationScheduleDTO request) {
        NotificationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        schedule.setTitle(request.getTitle());
        schedule.setBody(request.getBody());
        schedule.setSendTime(request.getSendTime());

        return scheduleMapper.toDto(scheduleRepository.save(schedule));
    }

    @Override
    public void deleteSchedule(Long id) {
        NotificationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
        scheduleRepository.delete(schedule);
    }

    @Override
    public List<NotificationScheduleSummaryDTO> getSchedulesByFestival(Long festivalId) {
        List<NotificationSchedule> list = scheduleRepository.findByFestival_FestivalId(festivalId);
        return scheduleMapper.toSummaryDtoList(list);
    }

    @Override
    public List<NotificationScheduleSummaryDTO> getAllSchedules() {
        return scheduleMapper.toSummaryDtoList(scheduleRepository.findAll());
    }
}