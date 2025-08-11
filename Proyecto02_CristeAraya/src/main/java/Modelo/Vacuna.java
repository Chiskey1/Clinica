package Modelo;

import java.time.LocalDate;

public class Vacuna {
    private Integer id;             // ID único
    private LocalDate fecha;        // Fecha de aplicación
    private String vacuna;          // Nombre de la vacuna
    private Persona aplicadoPor;    // Doctor o Enfermero que aplicó
    private Paciente paciente;      // Paciente vacunado

    public Vacuna(Integer id, LocalDate fecha, String vacuna, Persona aplicadoPor, Paciente paciente) {
        this.id = id;
        this.fecha = fecha;
        this.vacuna = vacuna;
        this.aplicadoPor = aplicadoPor;
        this.paciente = paciente;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public String getVacuna() { return vacuna; }
    public Persona getAplicadoPor() { return aplicadoPor; }
    public Paciente getPaciente() { return paciente; }
}
