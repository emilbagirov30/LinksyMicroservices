package com.emil.linksy_user.repository;

import com.emil.linksy_user.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
}
