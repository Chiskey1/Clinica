package Modelo;

import java.time.LocalDateTime;

public class Cita {
    // ID
    private Integer id;
    // Paciente
    private Paciente paciente;
    // Doctor
    private Doctor doctor;
    // Fecha y hora
    private LocalDateTime fechaHora;

    public Cita(Integer id, Paciente paciente, Doctor doctor, LocalDateTime fechaHora) {
        this.id = id;
        this.paciente = paciente;
        this.doctor = doctor;
        this.fechaHora = fechaHora;
    }

    // Obtener ID
    public Integer getId() { return id; }
    // Asignar ID
    public void setId(Integer id) { this.id = id; }

    // Obtener paciente
    public Paciente getPaciente() { return paciente; }
    // Asignar paciente
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    // Obtener doctor
    public Doctor getDoctor() { return doctor; }
    // Asignar doctor
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    // Obtener fecha y hora
    public LocalDateTime getFechaHora() { return fechaHora; }
    // Asignar fecha y hora
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
