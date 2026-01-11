package com.example.hospital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.hospital.model.Appointment;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.Patient;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AppointmentRepository appointmentRepository;
    

    @GetMapping("/create-doctor")
    @ResponseBody
    public String createDoctor() {
        Doctor d = new Doctor();
        d.setName("Dr Kumar");
        d.setEmail("kumar@hospital.com");
        d.setPassword(passwordEncoder.encode("doctor123"));
        d.setSpecialization("Cardiology");

        doctorRepository.save(d);
        return "Doctor created successfully";
    }

    // Show login page
    @GetMapping("/doctor/login")
    public String showLogin() {
        return "doctor-login";
    }

    // Handle login
    @PostMapping("/doctor/login")
    public String loginDoctor(
        @RequestParam String email,
        @RequestParam String password,
        Model model,
        HttpSession session) {

    Doctor doctor = doctorRepository.findByEmail(email);

    if (doctor == null || !passwordEncoder.matches(password, doctor.getPassword())) {
        model.addAttribute("error", "Invalid email or password");
        return "doctor-login";
    }

    session.setAttribute("doctor", doctor); // 🔥 STORE IN SESSION
    return "redirect:/doctor/dashboard/" + doctor.getId();
    }

    @GetMapping("/doctor/dashboard/{id}")
    public String doctorDashboard(
        @PathVariable String id,
        HttpSession session,
        Model model) {

    Doctor doctor = (Doctor) session.getAttribute("doctor");
    if (doctor == null) {
        return "redirect:/doctor/login";
    }

    long pendingCount =
        appointmentRepository.countByDoctorIdAndStatus(
            doctor.getId(),
            "PENDING"
        );

    model.addAttribute("doctor", doctor);
    model.addAttribute("pendingCount", pendingCount); // 🔥 IMPORTANT

    return "doctor-dashboard";
    }

    @GetMapping("/doctor/patient-profile/{id}")
    public String viewPatientProfile(
        @PathVariable String id,
        HttpSession session,
        Model model) {

    if (session.getAttribute("doctor") == null) {
        return "redirect:/doctor/login";
    }

    Patient patient = patientRepository.findById(id).orElse(null);
    model.addAttribute("patient", patient);

    return "patient-profile-view";
    }

    @GetMapping("/doctor/appointments/{doctorId}")
    public String viewAppointments(@PathVariable String doctorId, Model model) {
        model.addAttribute(
            "appointments",
            appointmentRepository.findByDoctorId(doctorId)
        );
    return "doctor-appointments";
    }
    
    @GetMapping("/doctor/requests/{id}")
    public String viewRequests(
        @PathVariable String id,
        HttpSession session,
        Model model) {

    Doctor doctor = (Doctor) session.getAttribute("doctor");
    if (doctor == null) {
        return "redirect:/doctor/login";
    }

    model.addAttribute("doctorId", doctor.getId());

    model.addAttribute(
        "requests",
        appointmentRepository.findByDoctorIdAndStatus(
            String.valueOf(doctor.getId()),
            "PENDING"
        )
    );

    return "doctor-requests";
    }

    @GetMapping("/doctor/appointments")
    public String viewDoctorAppointments(HttpSession session, Model model) {

    Doctor doctor = (Doctor) session.getAttribute("doctor");
    if (doctor == null) {
        return "redirect:/doctor/login";
    }

    model.addAttribute("doctor", doctor);

    model.addAttribute(
        "appointments",
        appointmentRepository.findByDoctorIdAndStatusIn(
            String.valueOf(doctor.getId()),
            List.of("ACCEPTED", "REJECTED")
        )
    );

    return "doctor-appointments";
    }

    @PostMapping("/doctor/accept")
    public String acceptAppointment(
        @RequestParam String appointmentId,
        @RequestParam String doctorId) {

    Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
    if (app != null) {
        app.setStatus("ACCEPTED");
        appointmentRepository.save(app);
    }

    return "redirect:/doctor/requests/" + doctorId;
    }

    @PostMapping("/doctor/reject")
    public String rejectAppointment(
        @RequestParam String appointmentId,
        @RequestParam String doctorId) {

    Appointment app = appointmentRepository.findById(appointmentId).orElse(null);
    if (app != null) {
        app.setStatus("REJECTED");
        appointmentRepository.save(app);
    }

    return "redirect:/doctor/requests/" + doctorId;
    }

    @GetMapping("/doctor/logout")
    public String doctorLogout(HttpSession session) {

    // Clear doctor session
    session.invalidate();

    // Redirect safely to login page
    return "redirect:/doctor/login";
    }

    @GetMapping("/doctor/change-password")
    public String changePasswordPage(HttpSession session) {

    if (session.getAttribute("doctor") == null) {
        return "redirect:/doctor/login";
    }

    return "doctor-change-password";
    }

    @PostMapping("/doctor/change-password")
    public String changePassword(
        @RequestParam String oldPassword,
        @RequestParam String newPassword,
        HttpSession session,
        Model model) {

    Doctor doctor = (Doctor) session.getAttribute("doctor");

    if (!passwordEncoder.matches(oldPassword, doctor.getPassword())) {
        model.addAttribute("error", "Old password incorrect");
        return "doctor-change-password";
    }

    doctor.setPassword(passwordEncoder.encode(newPassword));
    doctorRepository.save(doctor);

    model.addAttribute("success", "Password changed successfully");
    return "doctor-change-password";
    }

}