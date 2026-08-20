package com.example.itday.domain.member.entity;

import com.example.itday.domain.membership.entity.Membership;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;

    @Column(name = "socialId", nullable = false, unique = true)
    private Long socialId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Setter
    @Column(name = "name")
    private String name;

    @Column(name = "profileImg")
    private String profileImg;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "point", nullable = false)
    private int point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membershipId")
    @Setter // onborading에서 추가하기 위함
    private Membership membership;

    public void addPoint(int point) {
        this.point += point;
    }
}
