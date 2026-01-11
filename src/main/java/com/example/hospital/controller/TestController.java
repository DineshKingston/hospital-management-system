package com.example.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.model.TestDocument;
import com.example.hospital.repository.TestRepository;

@RestController
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @GetMapping("/test-db")
    public String testMongoDB() {
        testRepository.save(new TestDocument("MongoDB Atlas Connected"));
        return "MongoDB Atlas connection SUCCESS ✅";
    }
}
