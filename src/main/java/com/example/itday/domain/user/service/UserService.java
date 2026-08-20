package com.example.itday.domain.user.service;

import com.example.itday.domain.user.dto.DuplicateCheckRequest;
import com.example.itday.domain.user.dto.DuplicateCheckResponse;
import com.example.itday.domain.user.entity.User;
import com.example.itday.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public DuplicateCheckResponse checkDuplicate(DuplicateCheckRequest request) {

        // email / nickname 중 하나라도 중복인지 조회
        Optional<User> existingUserOpt =
                userRepository.findByEmailOrNickname(
                        request.getEmail(),
                        request.getNickname()
                );

        // 중복 없음
        if (existingUserOpt.isEmpty()) {
            return new DuplicateCheckResponse(
                    false,
                    List.of(),
                    "사용 가능한 정보입니다."
            );
        }

        User existingUser = existingUserOpt.get();
        List<String> duplicates = new ArrayList<>();

        if (request.getNickname() != null
                && request.getNickname().equals(existingUser.getNickname())) {
            duplicates.add("닉네임");
        }

        if (request.getEmail() != null
                && request.getEmail().equals(existingUser.getEmail())) {
            duplicates.add("이메일");
        }

        String message =
                "이미 사용중인 " + String.join(", ", duplicates) + "입니다.";

        return new DuplicateCheckResponse(
                true,
                duplicates,
                message
        );
    }

    public DuplicateCheckResponse checkSingleField(
            String type,
            String value
    ) {

        boolean isDuplicate;
        String fieldName;

        switch (type) {

            case "nickname":
                fieldName = "닉네임";
                isDuplicate = userRepository.existsByNickname(value);
                break;

            case "email":
                fieldName = "이메일";
                isDuplicate = userRepository.existsByEmail(value);
                break;

            default:
                return new DuplicateCheckResponse(
                        false,
                        List.of(),
                        "지원하지 않는 중복 확인 항목입니다."
                );
        }

        if (isDuplicate) {
            return new DuplicateCheckResponse(
                    true,
                    List.of(fieldName),
                    "이미 사용 중인 " + fieldName + "입니다."
            );
        }

        return new DuplicateCheckResponse(
                false,
                List.of(),
                "사용 가능한 " + fieldName + "입니다."
        );
    }
}
