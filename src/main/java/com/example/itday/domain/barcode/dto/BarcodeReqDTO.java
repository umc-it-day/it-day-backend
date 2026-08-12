package com.example.itday.domain.barcode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BarcodeReqDTO(
        @NotBlank
        @Pattern(regexp = "^\\d{16}$", message = "바코드 번호는 16자리 숫자여야 합니다.")
        String barcodeNum
) {
}
