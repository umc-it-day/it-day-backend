package com.example.itday.domain.point.entity;

import com.example.itday.domain.member.entity.Member;
import com.example.itday.domain.point.enums.PointReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pointhistory")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pointHistoryId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(name = "rewardPoint", nullable = false)
    private int rewardPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private PointReason reason;

    @Column(name = "createdAt", nullable = false)
    private LocalDate createdAt;
}
