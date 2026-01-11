package com.example.hospital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.hospital.model.Doctor;

public interface DoctorRepository extends MongoRepository<Doctor, String> {

    Doctor findByEmail(String email);
}
