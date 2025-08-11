package Controlador;

import Modelo.*;
import dao.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppController {

    // Instancias de los DAOs para acceso a datos de cada entidad
    private final PacienteDAO       pacienteDAO       = new PacienteDAO();
    private final DoctorDAO         doctorDAO         = new DoctorDAO();
    private final EnfermeroDAO      enfermeroDAO      = new EnfermeroDAO();
    private final CitaDAO           citaDAO           = new CitaDAO();
    private final HojaMedicaDAO     hojaMedicaDAO     = new HojaMedicaDAO();
    private final HojaEnfermeriaDAO hojaEnfermeriaDAO = new HojaEnfermeriaDAO();
    private final VacunaDAO         vacunaDAO         = new VacunaDAO();

    // ===== MÉTODOS PARA PACIENTES =====

    // Agrega un nuevo paciente creando objeto y llamando DAO
    public void addPaciente(String n, String t, String s, String d, String e) {
        try {
            Paciente p = new Paciente(n, t, s, d, e);   // id = null porque es nuevo
            pacienteDAO.insertar(p);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar paciente", ex);
        }
    }

    // Devuelve la lista completa de pacientes
    public List<Paciente> getPacientes() {
        try { return pacienteDAO.listar(); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar pacientes", ex); }
    }

    // Busca un paciente por su id
    public Paciente findPacienteById(int id) {
        try { return pacienteDAO.buscarPorId(id); }
        catch (SQLException ex) { throw new RuntimeException("Error buscando paciente por id", ex); }
    }

    // Busca paciente por email
    public Paciente findPacienteByEmail(String email) {
        try { return pacienteDAO.buscarPorEmail(email); }
        catch (SQLException ex) { throw new RuntimeException("Error buscando paciente por email", ex); }
    }

    // Elimina paciente por id
    public void eliminarPaciente(int id) {
        try {
            pacienteDAO.eliminar(id);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar paciente", ex);
        }
    }

    // ===== MÉTODOS PARA DOCTORES =====

    // Agrega un nuevo doctor con sus datos y especialidad
    public void addDoctor(String n, String t, String s, String d, String e, String esp) {
        try {
            Doctor doc = new Doctor(n, t, s, d, e, esp);
            doctorDAO.insertar(doc);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar doctor", ex);
        }
    }

    // Devuelve la lista completa de doctores
    public List<Doctor> getDoctores() {
        try { return doctorDAO.listar(); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar doctores", ex); }
    }

    // Busca un doctor por su id
    public Doctor findDoctorById(int id) {
        try { return doctorDAO.buscarPorId(id); }
        catch (SQLException ex) { throw new RuntimeException("Error buscando doctor por id", ex); }
    }

    // Elimina doctor por id
    public void eliminarDoctor(int id) {
        try {
            doctorDAO.eliminar(id);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar doctor", ex);
        }
    }

    // ===== MÉTODOS PARA ENFERMEROS =====

    // Agrega un nuevo enfermero con sus datos
    public void addEnfermero(String n, String t, String s, String d, String e) {
        try {
            Enfermero enf = new Enfermero(n, t, s, d, e);
            enfermeroDAO.insertar(enf);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar enfermero", ex);
        }
    }

    // Devuelve la lista completa de enfermeros
    public List<Enfermero> getEnfermeros() {
        try { return enfermeroDAO.listar(); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar enfermeros", ex); }
    }

    // Busca un enfermero por su id
    public Enfermero findEnfermeroById(int id) {
        try { return enfermeroDAO.buscarPorId(id); }
        catch (SQLException ex) { throw new RuntimeException("Error buscando enfermero por id", ex); }
    }

    // Elimina enfermero por id
    public void eliminarEnfermero(int id) {
        try {
            enfermeroDAO.eliminar(id);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar enfermero", ex);
        }
    }

    // ===== MÉTODOS PARA CITAS =====

    // Añade una cita con paciente, doctor y fecha/hora
    public void addCita(Paciente p, Doctor d, LocalDateTime fechaHora) {
        try {
            Cita c = new Cita(null, p, d, fechaHora);
            citaDAO.insertar(c);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar cita", ex);
        }
    }

    // Devuelve la lista completa de citas
    public List<Cita> getCitas() {
        try { return citaDAO.listar(); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar citas", ex); }
    }

    // Lista citas filtrando por paciente
    public List<Cita> getCitasPorPaciente(int pacienteId) {
        try { return citaDAO.listarPorPaciente(pacienteId); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar citas por paciente", ex); }
    }

    // Lista citas filtrando por doctor
    public List<Cita> getCitasPorDoctor(int doctorId) {
        try { return citaDAO.listarPorDoctor(doctorId); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar citas por doctor", ex); }
    }

    // Elimina cita por id
    public void eliminarCita(int id) {
        try {
            citaDAO.eliminar(id);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar cita", ex);
        }
    }

    // ===== MÉTODOS PARA HOJA MÉDICA =====

    // Añade hoja médica con detalles, doctor y paciente
    public void addHojaMedica(LocalDateTime fh, String motivo, String diag, String trat, Doctor doc, Paciente pac) {
        try {
            HojaMedica h = new HojaMedica(null, fh, motivo, diag, trat, doc, pac);
            hojaMedicaDAO.insertar(h);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar hoja médica", ex);
        }
    }

    // Devuelve la lista completa de hojas médicas
    public List<HojaMedica> getHojasMedicas() {
        try { return hojaMedicaDAO.listar(); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar hojas médicas", ex); }
    }

    // Filtra hojas médicas por paciente
    public List<HojaMedica> getHojasMedicasPorPaciente(int pacienteId) {
        try { return hojaMedicaDAO.listarPorPaciente(pacienteId); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar hojas médicas por paciente", ex); }
    }

    // Filtra hojas médicas por doctor
    public List<HojaMedica> getHojasMedicasPorDoctor(int doctorId) {
        try { return hojaMedicaDAO.listarPorDoctor(doctorId); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar hojas médicas por doctor", ex); }
    }

    // Elimina hoja médica por id
    public void eliminarHojaMedica(int id) {
        try {
            hojaMedicaDAO.eliminar(id);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar hoja médica", ex);
        }
    }

    // ===== MÉTODOS PARA HOJA DE ENFERMERÍA =====

    // Añade hoja de enfermería con datos, enfermero y paciente
    public void addHojaEnfermeria(LocalDateTime fh, String sv, String obs, Enfermero enf, Paciente pac) {
        try {
            HojaEnfermeria h = new HojaEnfermeria(null, fh, sv, obs, enf, pac);
            hojaEnfermeriaDAO.insertar(h);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar hoja de enfermería", ex);
        }
    }

    // Devuelve la lista completa de hojas de enfermería
    public List<HojaEnfermeria> getHojasEnfermeria() {
        try { return hojaEnfermeriaDAO.listar(); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar hojas de enfermería", ex); }
    }

    // Filtra hojas de enfermería por paciente
    public List<HojaEnfermeria> getHojasEnfermeriaPorPaciente(int pacienteId) {
        try { return hojaEnfermeriaDAO.listarPorPaciente(pacienteId); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar hojas de enfermería por paciente", ex); }
    }

    // Filtra hojas por enfermero
    public List<HojaEnfermeria> getHojasEnfermeriaPorEnfermero(int enfermeroId) {
        try { return hojaEnfermeriaDAO.listarPorEnfermero(enfermeroId); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar hojas por enfermero", ex); }
    }

    // Elimina hoja de enfermería por id
    public void eliminarHojaEnfermeria(int id) {
        try {
            hojaEnfermeriaDAO.eliminar(id);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar hoja de enfermería", ex);
        }
    }

    // ===== MÉTODOS PARA VACUNAS =====

    // Añade una vacuna con fecha, nombre, aplicador (persona) y paciente
    public void addVacuna(LocalDate fecha, String nombreVacuna, Persona aplicador, Paciente pac) {
        try {
            Vacuna v = new Vacuna(null, fecha, nombreVacuna, aplicador, pac);
            vacunaDAO.insertar(v);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar vacuna", ex);
        }
    }

    // Devuelve la lista completa de vacunas
    public List<Vacuna> getVacunas() {
        try { return vacunaDAO.listar(); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar vacunas", ex); }
    }

    // Filtra vacunas por paciente
    public List<Vacuna> getVacunasPorPaciente(int pacienteId) {
        try { return vacunaDAO.listarPorPaciente(pacienteId); }
        catch (SQLException ex) { throw new RuntimeException("Error al listar vacunas por paciente", ex); }
    }

    // Ejemplo de eliminación directa con SQL para vacuna (no usado en el resto)
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Vacuna WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }

    // Elimina vacuna por id usando DAO
    public void eliminarVacuna(int id) {
        try {
            vacunaDAO.eliminar(id);
        } catch (SQLException ex) {
            throw new RuntimeException("Error al eliminar vacuna", ex);
        }
    }

}
