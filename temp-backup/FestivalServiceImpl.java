package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.exception.global.BusinessException;
import com.tekcit.festival.domain.exception.global.ErrorCode;
import com.tekcit.festival.domain.user.entity.Festival;
import com.tekcit.festival.domain.user.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class FestivalServiceImpl implements FestivalService {
    private final FestivalRepository festivalRepository;

    @Override
    public Festival createFestival(Festival festival) {
        return festivalRepository.save(festival);
    }

    @Override
    public Festival updateFestival(Long festivalId, Festival request) {
        Festival festival = festivalRepository.findByFestivalId(festivalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FESTIVAL_NOT_FOUND));

        festival.setFname(request.getFname());
        festival.setFdfrom(request.getFdfrom());
        festival.setFdto(request.getFdto());
        festival.setPoster(request.getPoster());
        festival.setFcltynm(request.getFcltynm());
        festival.setArea(request.getArea());
        festival.setGenrename(request.getGenrename());
        festival.setOpenrun(request.getOpenrun());
        festival.setFstate(request.getFstate());

        return festivalRepository.save(festival);
    }

    @Override
    public void deleteFestivalByHost(Long festivalId, Long hostId) {
        Festival festival = festivalRepository.findByFestivalId(festivalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FESTIVAL_NOT_FOUND));

        if (!festival.getHostId().equals(hostId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        festivalRepository.delete(festival);
    }

    @Override
    public List<Festival> getFestivalsByHost(Long hostId) {
        return festivalRepository.findByHostId(hostId);
    }

    @Override
    public List<Festival> getAllFestivals() {
        return festivalRepository.findAll();
    }

    @Override
    public void adminDeleteFestival(Long festivalId) {
        Festival festival = festivalRepository.findByFestivalId(festivalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FESTIVAL_NOT_FOUND));
        festivalRepository.delete(festival);
    }

    @Override
    public Optional<Festival> getFestivalDetail(Long festivalId) {
        return festivalRepository.findByFestivalId(festivalId);
    }

    @Override
    public List<String> getCategories() {
        return festivalRepository.findDistinctGenrename();
    }
}