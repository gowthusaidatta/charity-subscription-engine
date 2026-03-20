package com.digitalheroes.golfcharity.charity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CharityRepository extends JpaRepository<Charity, UUID> {
    List<Charity> findByActiveTrueOrderByNameAsc();
}
