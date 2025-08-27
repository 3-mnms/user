package com.tekcit.festival.utils;

import com.tekcit.festival.domain.user.enums.UserGender;
import com.tekcit.festival.exception.BusinessException;
import com.tekcit.festival.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResidentUtil {
    private static final int MAX_AGE = 120;

    public static int calcAge(String residentNum){
        if (residentNum == null || !residentNum.contains("-")) {
            throw new BusinessException(ErrorCode.INVALID_RESIDENT_NUMBER);
        }

        String[] rArray = residentNum.split("-", 2);
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
            default:
                throw new BusinessException(ErrorCode.INVALID_RESIDENT_NUMBER);
        }

        int currentYear = LocalDate.now().getYear();
        if(currentYear<year)
            throw new BusinessException(ErrorCode.INVALID_RESIDENT_NUMBER);

        int age = currentYear-year+1;
        if(age>MAX_AGE)
            throw new BusinessException(ErrorCode.INVALID_RESIDENT_NUMBER);

        return age;
    }

    public static String calcBirth(String residentNum){
        if (residentNum == null || !residentNum.contains("-")) {
            throw new BusinessException(ErrorCode.INVALID_RESIDENT_NUMBER);
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
