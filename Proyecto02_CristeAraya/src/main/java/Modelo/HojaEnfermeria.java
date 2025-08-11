package Modelo;

import java.time.LocalDateTime;

public class HojaEnfermeria {
    private Integer id;                // ID de la hoja
    private LocalDateTime fechaHora;  // Fecha y hora de registro
    private String signosVitales;     // Signos vitales del paciente
    private String observaciones;     // Observaciones hechas
    private Enfermero enfermero;      // Enfermero responsable
    private Paciente paciente;        // Paciente asociado

    // Constructor
    public HojaEnfermeria(Integer id, LocalDateTime fechaHora, String signosVitales,
                          String observaciones, Enfermero enfermero, Paciente paciente) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.signosVitales = signosVitales;
        this.observaciones = observaciones;
        this.enfermero = enfermero;
        this.paciente = paciente;
    }

    // Getters y setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getSignosVitales() { return signosVitales; }
    public String getObservaciones() { return observaciones; }
    public Enfermero getEnfermero() { return enfermero; }
    public Paciente getPaciente() { return paciente; }
}
