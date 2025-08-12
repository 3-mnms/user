package com.tekcit.festival.utils;

import com.tekcit.festival.domain.user.enums.UserGender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResidentUtil {
    public static int calcAge(String residentNum){
        if (residentNum == null || !residentNum.contains("-")) {
            throw new IllegalArgumentException("올바르지 않은 주민번호 형식입니다.");
        }

        String[] rArray = residentNum.split("-");
        String birth = rArray[0];
        char gender = rArray[1].charAt(0);

        int year = Integer.parseInt(birth.substring(0, 2));

        switch(gender) {
            case '1': case '2': case '5': case '6':
                year += 1900;
                break;
            case '3': case '4': case '7': case '8':
                year += 2000;
                break;
            case '9': case '0':
                year += 1800;
                break;
            default:
                throw new IllegalArgumentException("올바르지 않은 성별 코드입니다.");
        }
        int currentYear = LocalDate.now().getYear();
        return currentYear-year+1;
    }

    public static String calcBirth(String residentNum){
        if (residentNum == null || !residentNum.contains("-")) {
            throw new IllegalArgumentException("올바르지 않은 주민번호 형식입니다.");
        }

        String[] rArray = residentNum.split("-");
        String birth = rArray[0];

        return birth;
    }

    public static UserGender extractGender(String residentNum){
        char g = residentNum.split("-")[1].charAt(0);
        UserGender gender = UserGender.MALE;

        if(g == '1' || g == '3'){
            gender = UserGender.MALE;
        }
        else if(g == '2' || g == '4'){
            gender = UserGender.FEMALE;
        }

        return gender;
    }
}
