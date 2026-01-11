package com.example.hospital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hospital.model.Admin;
import com.example.hospital.model.Doctor;
import com.example.hospital.repository.AdminRepository;
import com.example.hospital.repository.DoctorRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /* ================= ADMIN LOGIN ================= */

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String adminLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Admin admin = adminRepository.findByEmail(email);

        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            model.addAttribute("error", "Invalid admin credentials");
            return "admin-login";
        }

        session.setAttribute("admin", admin);
        return "redirect:/admin/dashboard";
    }

    /* ================= ADMIN DASHBOARD ================= */

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        return "admin-dashboard";
    }

    @PostMapping("/admin/add-doctor")
    public String addDoctor(
        @ModelAttribute Doctor doctor,
        HttpSession session,
        Model model) {

    if (session.getAttribute("admin") == null) {
        return "redirect:/admin/login";
    }

    Doctor existing = doctorRepository.findByEmail(doctor.getEmail());
    if (existing != null) {
        model.addAttribute("error", "Doctor email already exists");
        model.addAttribute("doctor", doctor);
        return "admin-add-doctor";
    }

    doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
    doctorRepository.save(doctor);

    return "redirect:/admin/doctors";
    }

    @GetMapping("/admin/add-doctor")
    public String showAddDoctor(HttpSession session, Model model) {

    if (session.getAttribute("admin") == null) {
        return "redirect:/admin/login";
    }

    model.addAttribute("doctor", new Doctor());
    return "admin-add-doctor";
    }

    @PostMapping("/admin/reset-doctor-password")
    public String resetDoctorPassword(
        @RequestParam String doctorId,
        @RequestParam String newPassword,
        HttpSession session) {

    if (session.getAttribute("admin") == null) {
        return "redirect:/admin/login";
    }

    Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

    if (doctor != null) {
        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctorRepository.save(doctor);
    }

    return "redirect:/admin/doctors";
    }

    /* ================= LIST DOCTORS ================= */

    @GetMapping("/admin/doctors")
    public String listDoctors(HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("doctors", doctorRepository.findAll());
        return "admin-doctors";
    }

    /* ================= DELETE DOCTOR ================= */

    @GetMapping("/admin/delete-doctor/{id}")
    public String deleteDoctor(
            @PathVariable String id,
            HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin/login";
        }

        doctorRepository.deleteById(id);
        return "redirect:/admin/doctors";
    }

    /* ================= LOGOUT ================= */

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}


