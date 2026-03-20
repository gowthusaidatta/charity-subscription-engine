package com.digitalheroes.golfcharity.charity;

import java.util.UUID;

public record CharityResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String imageUrl,
        boolean active,
        boolean featured
) {
}
