package Modelo;

public abstract class Persona {
    protected Integer id;         // ID único (PK)
    protected String  nombre;
    protected String  telefono;
    protected String  sexo;       // "M", "F", "Otro"
    protected String  domicilio;
    protected String  email;

    // Constructor sin id (se asigna luego)
    public Persona(String nombre, String telefono, String sexo, String domicilio, String email) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.sexo = sexo;
        this.domicilio = domicilio;
        this.email = email;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return nombre + " (" + email + ")";
    }
}
