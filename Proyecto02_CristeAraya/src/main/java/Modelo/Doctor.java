package Modelo;

public class Doctor extends Persona {
    // Especialidad del doctor
    private String especialidad;

    // Constructor
    public Doctor(String nombre, String telefono, String sexo, String domicilio, String email, String especialidad) {
        super(nombre, telefono, sexo, domicilio, email);
        this.especialidad = especialidad;
    }

    // Obtener especialidad
    public String getEspecialidad() { return especialidad; }
    // Asignar especialidad
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    // Representación en texto
    @Override public String toString() {
        return super.toString() + " - Esp: " + especialidad;
    }
}
