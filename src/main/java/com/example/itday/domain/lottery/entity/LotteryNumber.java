package com.example.itday.domain.lottery.entity;

import com.example.itday.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lotterynumber")
public class LotteryNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lotteryNumberId")
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "ranking", nullable = false)
    private int ranking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Column(name = "assignedAt")
    private LocalDateTime assignedAt;

    public void assignTo(Member member) {
        this.member = member;
        this.assignedAt = LocalDateTime.now();
    }

    public void assignMember(Long memberId) {
    }
}