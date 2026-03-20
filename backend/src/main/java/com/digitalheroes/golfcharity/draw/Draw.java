package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.enums.DrawMode;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "draws")
public class Draw {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 7)
    private String monthKey;

    @Column(nullable = false)
    private LocalDate drawDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DrawMode mode;

    @Column(nullable = false, length = 100)
    private String winningNumbers;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private Integer activeSubscriberCount = 0;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPoolAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal tier5PoolAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal tier4PoolAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal tier3PoolAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal rolloverInAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal rolloverOutAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCharityContributionAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getMonthKey() {
        return monthKey;
    }

    public void setMonthKey(String monthKey) {
        this.monthKey = monthKey;
    }

    public LocalDate getDrawDate() {
        return drawDate;
    }

    public void setDrawDate(LocalDate drawDate) {
        this.drawDate = drawDate;
    }

    public DrawMode getMode() {
        return mode;
    }

    public void setMode(DrawMode mode) {
        this.mode = mode;
    }

    public String getWinningNumbers() {
        return winningNumbers;
    }

    public void setWinningNumbers(String winningNumbers) {
        this.winningNumbers = winningNumbers;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Integer getActiveSubscriberCount() {
        return activeSubscriberCount;
    }

    public void setActiveSubscriberCount(Integer activeSubscriberCount) {
        this.activeSubscriberCount = activeSubscriberCount;
    }

    public BigDecimal getTotalPoolAmount() {
        return totalPoolAmount;
    }

    public void setTotalPoolAmount(BigDecimal totalPoolAmount) {
        this.totalPoolAmount = totalPoolAmount;
    }

    public BigDecimal getTier5PoolAmount() {
        return tier5PoolAmount;
    }

    public void setTier5PoolAmount(BigDecimal tier5PoolAmount) {
        this.tier5PoolAmount = tier5PoolAmount;
    }

    public BigDecimal getTier4PoolAmount() {
        return tier4PoolAmount;
    }

    public void setTier4PoolAmount(BigDecimal tier4PoolAmount) {
        this.tier4PoolAmount = tier4PoolAmount;
    }

    public BigDecimal getTier3PoolAmount() {
        return tier3PoolAmount;
    }

    public void setTier3PoolAmount(BigDecimal tier3PoolAmount) {
        this.tier3PoolAmount = tier3PoolAmount;
    }

    public BigDecimal getRolloverInAmount() {
        return rolloverInAmount;
    }

    public void setRolloverInAmount(BigDecimal rolloverInAmount) {
        this.rolloverInAmount = rolloverInAmount;
    }

    public BigDecimal getRolloverOutAmount() {
        return rolloverOutAmount;
    }

    public void setRolloverOutAmount(BigDecimal rolloverOutAmount) {
        this.rolloverOutAmount = rolloverOutAmount;
    }

    public BigDecimal getTotalCharityContributionAmount() {
        return totalCharityContributionAmount;
    }

    public void setTotalCharityContributionAmount(BigDecimal totalCharityContributionAmount) {
        this.totalCharityContributionAmount = totalCharityContributionAmount;
    }
}
