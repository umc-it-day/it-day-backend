package com.example.itday.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateCheckRequest {
    private String username;
    private String nickname;
    private String phoneNumber;
    private String email;
}
