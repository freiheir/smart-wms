package com.smart.wmserp.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

/**
 * 품번 정제 유틸리티 (Part Number Cleaning)
 * - '-' (하이픈) 제거
 * - 'E' (지수 표기법 또는 특정 접미사) 제거 로직 적용
 */
@Component
public class PartNumberUtil {

    private static final Pattern CLEAN_PATTERN = Pattern.compile("[ -E]");

    /**
     * 품번에서 '-', ' ', 'E'를 제거하여 정제된 문자열 반환
     * 
     * @param rawPartNumber 원본 품번
     * @return 정제된 품번
     */
    public static String clean(String rawPartNumber) {
        if (rawPartNumber == null) return null;
        
        // '-' 제거, ' ' 제거, 'E' 제거 (대문자 기준)
        return rawPartNumber.replace("-", "")
                           .replace(" ", "")
                           .replace("E", "")
                           .toUpperCase()
                           .trim();
    }
}
