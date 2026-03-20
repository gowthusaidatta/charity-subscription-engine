package com.digitalheroes.golfcharity.notification;

import com.digitalheroes.golfcharity.enums.PayoutStatus;
import com.digitalheroes.golfcharity.enums.VerificationStatus;
import com.digitalheroes.golfcharity.winner.Winner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.notifications.email-enabled:false}")
    private boolean emailEnabled;

    @Value("${app.notifications.from-email:no-reply@golfcharity.local}")
    private String fromEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void notifyDrawPublished(String monthKey, List<Winner> winners) {
        if (!emailEnabled) {
            return;
        }

        for (Winner winner : winners) {
            sendMail(
                    winner.getUser().getEmail(),
                    "Draw Results: " + monthKey,
                    "Your draw result is available. Match count: " + winner.getMatchCount() +
                            ", Prize: " + winner.getPrizeAmount()
            );
        }
    }

    public void notifyWinnerVerification(Winner winner) {
        if (!emailEnabled) {
            return;
        }

        VerificationStatus status = winner.getVerificationStatus();
        sendMail(
                winner.getUser().getEmail(),
                "Winner Verification Update",
                "Your winner verification status is now: " + status
        );
    }

    public void notifyWinnerPayout(Winner winner) {
        if (!emailEnabled) {
            return;
        }

        PayoutStatus status = winner.getPayoutStatus();
        sendMail(
                winner.getUser().getEmail(),
                "Payout Status Update",
                "Your payout status is now: " + status + ". Prize amount: " + winner.getPrizeAmount()
        );
    }

    private void sendMail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ignored) {
            // Notification delivery failures should not block core transactional flows.
        }
    }
}
