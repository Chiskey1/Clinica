package Modelo;

import java.time.LocalDateTime;

public class HojaMedica {
    private Integer id;                // ID de la hoja médica
    private LocalDateTime fechaHora;  // Fecha y hora de la consulta
    private String motivoConsulta;    // Motivo de la consulta
    private String diagnosticos;      // Diagnósticos realizados
    private String tratamiento;       // Tratamiento recomendado
    private Doctor doctor;            // Doctor responsable
    private Paciente paciente;        // Paciente atendido

    // Constructor
    public HojaMedica(Integer id, LocalDateTime fechaHora, String motivoConsulta,
                      String diagnosticos, String tratamiento, Doctor doctor, Paciente paciente) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.motivoConsulta = motivoConsulta;
        this.diagnosticos = diagnosticos;
        this.tratamiento = tratamiento;
        this.doctor = doctor;
        this.paciente = paciente;
    }

    // Getters y setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getDiagnosticos() { return diagnosticos; }
    public String getTratamiento() { return tratamiento; }
    public Doctor getDoctor() { return doctor; }
    public Paciente getPaciente() { return paciente; }
}
