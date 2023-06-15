package com.example.wagesystem.repository;

import com.example.wagesystem.domain.Resignation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResignationRepository extends JpaRepository<Resignation, Long> {
}
