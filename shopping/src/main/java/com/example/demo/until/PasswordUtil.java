package com.example.demo.until;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordUtil {

    /**
     * 임의의 비밀번호를 생성하는 메서드
     * @return 16자리의 영숫자 비밀번호
     */
    public static String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(16); // 16자리의 영숫자로 된 랜덤 비밀번호 생성
    }
}