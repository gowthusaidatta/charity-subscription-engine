package com.digitalheroes.golfcharity.winner;

import com.digitalheroes.golfcharity.draw.Draw;
import com.digitalheroes.golfcharity.enums.PayoutStatus;
import com.digitalheroes.golfcharity.enums.VerificationStatus;
import com.digitalheroes.golfcharity.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "winners")
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer matchCount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prizeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;

    @Column(length = 500)
    private String proofUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Draw getDraw() {
        return draw;
    }

    public void setDraw(Draw draw) {
        this.draw = draw;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }

    public BigDecimal getPrizeAmount() {
        return prizeAmount;
    }

    public void setPrizeAmount(BigDecimal prizeAmount) {
        this.prizeAmount = prizeAmount;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public PayoutStatus getPayoutStatus() {
        return payoutStatus;
    }

    public void setPayoutStatus(PayoutStatus payoutStatus) {
        this.payoutStatus = payoutStatus;
    }

    public String getProofUrl() {
        return proofUrl;
    }

    public void setProofUrl(String proofUrl) {
        this.proofUrl = proofUrl;
    }
}
