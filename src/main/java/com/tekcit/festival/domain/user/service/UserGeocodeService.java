package com.tekcit.festival.domain.user.service;

import com.tekcit.festival.domain.user.dto.response.KakaoMapResponseDTO;
import com.tekcit.festival.domain.user.entity.Address;
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
    public boolean geocode(Address address){
        log.info("geocode 시작", address.getId());

        String searchAddress = normalizeAddress(address.getAddress());
        log.info("searchAddress:{}", searchAddress);
        KakaoMapResponseDTO response = kakaoSearchService.geocodeAddress(searchAddress)
                .orElse(null);

        if(response != null) {
            address.setLongitude(Double.parseDouble(response.getLongitude()));
            address.setLatitude(Double.parseDouble(response.getLatitude()));
            address.setIsGeocoded(GeocodeStatus.SUCCESS);
            addressRepository.save(address);
            return true;
        }
        else
            return false;
    }

    @Transactional
    public int geocodeBatch(int size){
        List<Address> addresses = addressRepository.findGeocoding(PageRequest.of(0, size, Sort.by(Sort.Direction.ASC, "id")));
        log.info("[GEOCODE] picked targets={}", addresses.size());

        int success = 0;
        for(Address address: addresses){
            log.info("[GEOCODE] start id={}, address={}", address.getId(), address.getAddress());
            try {
                if (geocode(address))
                    success++;
                else {
                    log.info("geocode 실패 (결과 없음)");
                    address.setIsGeocoded(GeocodeStatus.NO_RESULT);
                    addressRepository.save(address);
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
