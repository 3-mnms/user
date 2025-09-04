package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.response.KakaoMapResponseDTO;
import com.tekcit.festival.domain.user.entity.Address;
import com.tekcit.festival.domain.user.entity.UserProfile;
import com.tekcit.festival.domain.user.enums.GeocodeStatus;
import com.tekcit.festival.domain.user.repository.AddressRepository;
import com.tekcit.festival.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserGeocodeService {
    private final UserProfileRepository userProfileRepository;
    private final AddressRepository addressRepository;
    private final KakaoSearchService kakaoSearchService;

    public String normalizeAddress(String address) {
        if (address == null)
            return null;
        return address.split(",")[0].trim();
    }

    @Transactional
    public boolean geocode(UserProfile userProfile){
        log.info("geocode 시작", userProfile.getUId());
        Address address = addressRepository.findDefaultByUserProfile(userProfile)
                .orElse(null);
        if(address == null) {
            log.info("기본 배송지가 없습니다.");
            return false;
        }
        String searchAddress = normalizeAddress(address.getAddress());
        log.info("searchAddress:{}", searchAddress);
        KakaoMapResponseDTO response = kakaoSearchService.geocodeAddress(searchAddress)
                .orElse(null);

        if(response != null) {
            userProfile.setLongitude(Double.parseDouble(response.getLongitude()));
            userProfile.setLatitude(Double.parseDouble(response.getLatitude()));
            userProfile.setIsGeocoded(GeocodeStatus.SUCCESS);
            userProfileRepository.save(userProfile);
            return true;
        }
        else
            return false;
    }

    @Transactional
    public int geocodeBatch(int size){
        List<UserProfile> userProfiles = userProfileRepository.findGeocoding(PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "uId")));
        log.info("[GEOCODE] picked targets={}", userProfiles.size());

        int success = 0;
        for(UserProfile userProfile: userProfiles){
            log.info("[GEOCODE] start id={}", userProfile.getUId());
            try {
                if (geocode(userProfile))
                    success++;
                else {
                    log.info("geocode 실패 (결과 없음)");
                    userProfile.setIsGeocoded(GeocodeStatus.NO_RESULT);
                    userProfileRepository.save(userProfile);
                }
                Thread.sleep(200);
            } catch (Exception e) {
                log.error("geocode 실패(오류 발생)", e);
            }
        }

        log.info("success: {}", success);
        return success;
    }
}
