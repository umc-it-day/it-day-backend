package com.example.itday.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INVALID_INPUT_VALUE(
            HttpStatus.BAD_REQUEST,
            "COMMON_001",
            "입력값이 올바르지 않습니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON_002",
            "서버 내부 오류가 발생했습니다."
    ),

    // 도전과제
    CHALLENGE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHALLENGE_001",
            "도전과제를 찾을 수 없습니다."
    ),

    CHALLENGE_ALREADY_JOINED(
            HttpStatus.CONFLICT,
            "CHALLENGE_002",
            "이미 참여한 도전과제입니다."
    ),

    CHALLENGE_NOT_ACTIVE(
            HttpStatus.BAD_REQUEST,
            "CHALLENGE_003",
            "현재 참여할 수 없는 도전과제입니다."
    ),

    USER_CHALLENGE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "CHALLENGE_004",
            "사용자의 도전과제 참여 정보를 찾을 수 없습니다."
    ),

    CHALLENGE_ALREADY_COMPLETED(
            HttpStatus.BAD_REQUEST,
            "CHALLENGE_005",
            "이미 완료한 도전과제입니다."
    ),

    CHALLENGE_PROGRESS_CANNOT_BE_NEGATIVE(
            HttpStatus.BAD_REQUEST,
            "CHALLENGE_006",
            "진행도 증가량은 0보다 커야 합니다."
    ),

    CHALLENGE_DATE_INVALID(
            HttpStatus.BAD_REQUEST,
            "CHALLENGE_007",
            "도전과제 종료 일시는 시작 일시보다 늦어야 합니다."
    ),

    // 리포트
    REPORT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "REPORT_001",
            "리포트를 찾을 수 없습니다."
    ),

    REPORT_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "REPORT_002",
            "해당 기간의 리포트가 이미 존재합니다."
    ),

    REPORT_DATE_INVALID(
            HttpStatus.BAD_REQUEST,
            "REPORT_003",
            "리포트 종료일은 시작일보다 빠를 수 없습니다."
    ),

    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH_001",
            "유효하지 않은 토큰입니다."
    ),

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,
            "AUTH_002",
            "만료된 토큰입니다. 다시 로그인해주세요."
    ),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER_001",
            "존재하지 않는 회원입니다."
    ),

    DUPLICATE_BARCODE(HttpStatus.CONFLICT,
            "BARCODE_001",
            "이미 등록된 바코드입니다."
    ),

    KAKAO_MAP_API_ERROR(HttpStatus.BAD_GATEWAY,
            "MAP_001",
            "카카오 지도 API 호출에 실패했습니다."
    ),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MAP_002",
            "매장을 찾을 수 없습니다."
    );

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(
            HttpStatus httpStatus,
            String code,
            String message
    ) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
