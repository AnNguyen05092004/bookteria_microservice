package com.an.profile_service.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import com.an.profile_service.entity.UserProfile;

@Repository
public interface UserProfileRepository extends Neo4jRepository<UserProfile, String> {
    UserProfile findByUserId(String userId);

    List<UserProfile> findAllByUsernameLike(String username);
}
