package com.example.hospital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.hospital.model.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> {

    Patient findByEmail(String email);
}
