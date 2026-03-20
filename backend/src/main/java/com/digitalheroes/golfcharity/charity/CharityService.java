package com.digitalheroes.golfcharity.charity;

import com.digitalheroes.golfcharity.common.ResourceNotFoundException;
import com.digitalheroes.golfcharity.user.User;
import com.digitalheroes.golfcharity.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final UserRepository userRepository;

    public CharityService(CharityRepository charityRepository, UserRepository userRepository) {
        this.charityRepository = charityRepository;
        this.userRepository = userRepository;
    }

    public List<CharityResponse> getActiveCharities() {
        return charityRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CharityResponse createCharity(CharityRequest request) {
        Charity charity = new Charity();
        applyRequest(charity, request);
        return toResponse(charityRepository.save(charity));
    }

    @Transactional
    public CharityResponse updateCharity(UUID charityId, CharityRequest request) {
        Charity charity = charityRepository.findById(charityId)
                .orElseThrow(() -> new ResourceNotFoundException("Charity not found"));
        applyRequest(charity, request);
        return toResponse(charityRepository.save(charity));
    }

    @Transactional
    public void deleteCharity(UUID charityId) {
        Charity charity = charityRepository.findById(charityId)
                .orElseThrow(() -> new ResourceNotFoundException("Charity not found"));
        charityRepository.delete(charity);
    }

    @Transactional
    public void selectCharity(String email, CharitySelectionRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Charity charity = charityRepository.findById(request.charityId())
                .orElseThrow(() -> new ResourceNotFoundException("Charity not found"));

        user.setSelectedCharity(charity);
        user.setCharityContributionPercent(request.contributionPercent());
        userRepository.save(user);
    }

    private void applyRequest(Charity charity, CharityRequest request) {
        charity.setName(request.name().trim());
        charity.setSlug(request.slug().trim().toLowerCase());
        charity.setDescription(request.description().trim());
        charity.setImageUrl(request.imageUrl());
        charity.setActive(request.active());
        charity.setFeatured(request.featured());
    }

    private CharityResponse toResponse(Charity charity) {
        return new CharityResponse(
                charity.getId(),
                charity.getName(),
                charity.getSlug(),
                charity.getDescription(),
                charity.getImageUrl(),
                charity.isActive(),
                charity.isFeatured()
        );
    }
}
