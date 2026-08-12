package com.example.itday.domain.challenge.entity;

import com.example.itday.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "tower_progresses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tower_progress_user",
                        columnNames = "user_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TowerProgress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tower_progress_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "current_floor", nullable = false)
    private Integer currentFloor;

    @Column(name = "highest_unlocked_floor", nullable = false)
    private Integer highestUnlockedFloor;

    @Column(name = "visual_code", nullable = false, length = 50)
    private String visualCode;

    @Builder
    private TowerProgress(Long userId) {
        this.userId = userId;
        this.currentFloor = 1;
        this.highestUnlockedFloor = 1;
        this.visualCode = "CAPYBARA_ALONE";
    }

    public void unlockFloor(
            int floor,
            String visualCode
    ) {
        if (floor > highestUnlockedFloor) {
            this.highestUnlockedFloor = floor;
            this.currentFloor = floor;
            this.visualCode = visualCode;
        }
    }
}
