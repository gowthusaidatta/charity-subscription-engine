package com.digitalheroes.golfcharity.draw;

import com.digitalheroes.golfcharity.enums.DrawMode;
import jakarta.persistence.*;

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
}
