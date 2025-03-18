package com.cts.myjobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.myjobportal.entity.JobSeekerProfile;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {
}
