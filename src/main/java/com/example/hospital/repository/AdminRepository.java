package com.example.hospital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.hospital.model.Admin;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Admin findByEmail(String email);
}

