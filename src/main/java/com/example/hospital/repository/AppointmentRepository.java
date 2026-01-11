package com.example.hospital.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.hospital.model.Appointment;

public interface AppointmentRepository
        extends MongoRepository<Appointment, String> {

    List<Appointment> findByDoctorId(String doctorId);

    List<Appointment> findByDoctorIdAndStatus(String doctorId, String status);

    List<Appointment> findByDoctorIdAndStatusIn(String doctorId, List<String> status);


    List<Appointment> findByPatientId(String patientId);

    boolean existsByPatientIdAndDoctorId(String patientId, String doctorId);

    @Override
    void deleteById(String id);

    long countByDoctorIdAndStatus(String doctorId, String status);

}
