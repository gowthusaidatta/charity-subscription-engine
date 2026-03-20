package com.digitalheroes.golfcharity.charity;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/charities")
public class CharityController {

    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }

    @GetMapping
    public List<CharityResponse> getCharities() {
        return charityService.getActiveCharities();
    }

    @PostMapping("/select")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void selectCharity(@Valid @RequestBody CharitySelectionRequest request, Authentication authentication) {
        charityService.selectCharity(authentication.getName(), request);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public CharityResponse createCharity(@Valid @RequestBody CharityRequest request) {
        return charityService.createCharity(request);
    }

    @PutMapping("/admin/{charityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CharityResponse updateCharity(@PathVariable UUID charityId, @Valid @RequestBody CharityRequest request) {
        return charityService.updateCharity(charityId, request);
    }

    @DeleteMapping("/admin/{charityId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCharity(@PathVariable UUID charityId) {
        charityService.deleteCharity(charityId);
    }
}
