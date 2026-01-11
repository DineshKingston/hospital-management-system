package com.example.hospital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.hospital.model.TestDocument;

public interface TestRepository extends MongoRepository<TestDocument, String> {
}
