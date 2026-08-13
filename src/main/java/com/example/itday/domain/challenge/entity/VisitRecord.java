package com.example.itday.domain.challenge.entity;

import com.example.itday.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "visit_records",
        indexes = {
                @Index(
                        name = "idx_visit_record_user_visited_at",
                        columnList = "user_id, visited_at"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_record_id")
    private Long id;

    /*
     * 로그인 담당 도메인과 충돌을 줄이기 위해
     * User 연관관계 대신 userId만 저장한다.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    @Builder
    private VisitRecord(
            Long userId,
            LocalDateTime visitedAt
    ) {
        this.userId = userId;
        this.visitedAt = visitedAt;
    }
}
