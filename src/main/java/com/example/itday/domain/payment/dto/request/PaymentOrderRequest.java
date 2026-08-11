package com.example.itday.domain.payment.dto.request;

import com.example.itday.domain.payment.entity.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentOrderRequest {

    @NotNull
    private PlanType planType;
}
