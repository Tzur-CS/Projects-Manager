package com.example.demo.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Project entity
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByOwnerId(String ownerId, Pageable pageable);

    Optional<Project> findByIdAndOwnerId(Long id, String ownerId);

    Page<Project> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

