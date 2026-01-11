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

import com.example.hospital.model.Appointment;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.Patient;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.PatientRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;

    public PatientController(DoctorRepository doctorRepository,
                            AppointmentRepository appointmentRepository,
                            PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    // Show signup page
    @GetMapping("/patient/signup")
    public String showSignup(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient-signup";
    }

    // Handle signup
    @PostMapping("/patient/signup")
    public String signup(@ModelAttribute Patient patient, Model model) {

        Patient existing = patientRepository.findByEmail(patient.getEmail());
        if (existing != null) {
            model.addAttribute("error", "Email already registered");
            return "patient-signup";
        }

        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patientRepository.save(patient);
        return "redirect:/patient/login";
    }

    // Show login page
    @GetMapping("/patient/login")
    public String showLogin() {
        return "patient-login";
    }

    // Handle login
    @PostMapping("/patient/login")
    public String login(
        @RequestParam String email,
        @RequestParam String password,
        Model model,
        HttpSession session) {

    Patient patient = patientRepository.findByEmail(email);

    if (patient == null || !passwordEncoder.matches(password, patient.getPassword())) {
        model.addAttribute("error", "Invalid email or password");
        return "patient-login";
    }

    session.setAttribute("patient", patient); // ✅ STORE IN SESSION
    return "redirect:/patient/dashboard";
    }

    @GetMapping("/patient/logout")
    public String logout(HttpSession session) {
        session.invalidate();   // 🔥 clear session completely
        return "redirect:/patient/login";
    }


    @PostMapping("/patient/request")
    public String requestAppointment(
        @RequestParam String doctorId,
        @RequestParam String patientId,
        @RequestParam String patientName) {

    var doctor = doctorRepository.findById(doctorId).orElse(null);
    if (doctor == null) return "redirect:/patient/dashboard";

    Appointment a = new Appointment();
    a.setDoctorId(doctor.getId());
    a.setDoctorName(doctor.getName());
    a.setPatientId(patientId);
    a.setPatientName(patientName);
    a.setStatus("PENDING");

    appointmentRepository.save(a);
    return "redirect:/patient/dashboard";
    }

    @GetMapping("/patient/cancel/{appointmentId}")
    public String cancelAppointment(
        @PathVariable String appointmentId,
        HttpSession session) {

    Patient patient = (Patient) session.getAttribute("patient");
    if (patient == null) {
        return "redirect:/patient/login";
    }

    Appointment app = appointmentRepository.findById(appointmentId).orElse(null);

    if (app != null && app.getStatus().equals("PENDING")
            && app.getPatientId().equals(patient.getId())) {

        appointmentRepository.deleteById(appointmentId);
    }

    return "redirect:/patient/appointments";
    }

    @GetMapping("/patient/profile")
    public String patientProfile(HttpSession session, Model model) {

    Patient patient = (Patient) session.getAttribute("patient");
    if (patient == null) {
        return "redirect:/patient/login";
    }

    model.addAttribute("patient", patient);
    return "patient-profile";
    }

    @GetMapping("/patient/doctor-profile/{id}")
    public String viewDoctorProfile(
        @PathVariable String id,
        HttpSession session,
        Model model) {

    if (session.getAttribute("patient") == null) {
        return "redirect:/patient/login";
    }

    Doctor doctor = doctorRepository.findById(id).orElse(null);
    model.addAttribute("doctor", doctor);

    return "doctor-profile";
    }

    @GetMapping("/patient/dashboard")
    public String patientDashboard(HttpSession session, Model model) {

    Patient patient = (Patient) session.getAttribute("patient");

    if (patient == null) {
        return "redirect:/patient/login";
    }

    model.addAttribute("patient", patient);
    return "patient-dashboard";
    }

    @GetMapping("/patient/appointments/{patientId}")
    public String viewPatientAppointments(
        @PathVariable String patientId,
        Model model) {

    model.addAttribute(
        "appointments",
        appointmentRepository.findByPatientId(patientId)
    );

    return "patient-appointments";
    }

    // 🔹 View Doctors Page
    @GetMapping("/patient/doctors")
    public String viewDoctors(Model model) {
        model.addAttribute("doctors", doctorRepository.findAll());
        return "patient-doctors";
    }

    @PostMapping("/patient/apply")
    public String applyDoctor(
        @RequestParam String doctorId,
        @RequestParam String appointmentDate,
        @RequestParam String appointmentTime,
        HttpSession session) {

    Patient patient = (Patient) session.getAttribute("patient");
    if (patient == null) {
        return "redirect:/patient/login";
    }

    Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
    if (doctor == null) {
        return "redirect:/patient/doctors";
    }

    Appointment app = new Appointment();
    app.setDoctorId(doctorId);
    app.setDoctorName(doctor.getName());
    app.setPatientId(patient.getId());
    app.setPatientName(patient.getName());

    app.setAppointmentDate(appointmentDate); // ✅
    app.setAppointmentTime(appointmentTime); // ✅
    app.setStatus("PENDING");

    appointmentRepository.save(app);

    return "redirect:/patient/appointments";
    }


    // ✅ My Appointments
    @GetMapping("/patient/appointments")
    public String viewAppointments(HttpSession session, Model model) {

    Patient patient = (Patient) session.getAttribute("patient");

    if (patient == null) {
        return "redirect:/patient/login";
    }

    model.addAttribute(
        "appointments",
        appointmentRepository.findByPatientId(patient.getId())
    );

    return "patient-appointments";
    }

}
