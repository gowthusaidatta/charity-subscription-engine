package com.digitalheroes.golfcharity.admin;

import com.digitalheroes.golfcharity.subscription.SubscriptionService;
import com.digitalheroes.golfcharity.subscription.SubscriptionStatusResponse;
import com.digitalheroes.golfcharity.subscription.SubscriptionUpdateRequest;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final AdminService adminService;

    public AdminController(UserService userService, SubscriptionService subscriptionService, AdminService adminService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<UserSummaryResponse> getUsers() {
        return userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/users/{userId}/subscription")
    public SubscriptionStatusResponse updateSubscription(
            @PathVariable UUID userId,
            @Valid @RequestBody SubscriptionUpdateRequest request
    ) {
        return subscriptionService.adminUpdateSubscription(userId, request);
    }

    @PutMapping("/winners/{winnerId}/verify")
    public void verifyWinner(@PathVariable UUID winnerId, @Valid @RequestBody WinnerAdminUpdateRequest request) {
        adminService.verifyWinner(winnerId, request);
    }

    @PutMapping("/winners/{winnerId}/pay")
    public void markWinnerPaid(@PathVariable UUID winnerId) {
        adminService.markPaid(winnerId);
    }

    private UserSummaryResponse toResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getSelectedCharity() != null ? user.getSelectedCharity().getName() : null,
                user.getCharityContributionPercent()
        );
    }
}
