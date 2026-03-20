package com.digitalheroes.golfcharity.auth;

import com.digitalheroes.golfcharity.enums.Role;

public record AuthResponse(
        String token,
        String email,
        Role role,
        String fullName
) {
}
