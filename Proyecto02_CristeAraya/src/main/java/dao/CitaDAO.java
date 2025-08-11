package dao;

import Modelo.Cita;
import Modelo.Doctor;
import Modelo.Paciente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    // Instancias para obtener pacientes y doctores cuando se consultan citas
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final DoctorDAO   doctorDAO   = new DoctorDAO();

    /** Inserta una nueva cita en la base de datos y asigna el id generado */
    public int insertar(Cita c) throws SQLException {
        String sql = "INSERT INTO Cita (paciente_id, doctor_id, fechaHora) VALUES (?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, c.getPaciente().getId());
            st.setInt(2, c.getDoctor().getId());
            st.setTimestamp(3, Timestamp.valueOf(c.getFechaHora())); // Convierte LocalDateTime a Timestamp
            st.executeUpdate();

            // Obtener el ID generado automáticamente por la base
            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se generó ID para Cita");
    }

    /** Elimina una cita según su id */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Cita WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            int affected = st.executeUpdate();
            if (affected == 0) {
                throw new SQLException("No se encontró cita con id: " + id);
            }
        }
    }

    /** Devuelve todas las citas ordenadas por fecha/hora descendente */
    public List<Cita> listar() throws SQLException {
        String sql = "SELECT id, paciente_id, doctor_id, fechaHora FROM Cita ORDER BY fechaHora DESC";
        List<Cita> out = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Obtiene los objetos completos de paciente y doctor
                Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                Doctor   d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();
                out.add(new Cita(rs.getInt("id"), p, d, fh));
            }
        }
        return out;
    }

    /** Busca una cita por su id */
    public Cita buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, paciente_id, doctor_id, fechaHora FROM Cita WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    Doctor   d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();
                    return new Cita(rs.getInt("id"), p, d, fh);
                }
            }
        }
        return null;  // No encontró la cita
    }

    /** Lista todas las citas de un paciente específico */
    public List<Cita> listarPorPaciente(int pacienteId) throws SQLException {
        String sql = "SELECT id, paciente_id, doctor_id, fechaHora FROM Cita WHERE paciente_id = ? ORDER BY fechaHora DESC";
        List<Cita> out = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, pacienteId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    Doctor   d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();
                    out.add(new Cita(rs.getInt("id"), p, d, fh));
                }
            }
        }
        return out;
    }

    /** Lista todas las citas de un doctor específico */
    public List<Cita> listarPorDoctor(int doctorId) throws SQLException {
        String sql = "SELECT id, paciente_id, doctor_id, fechaHora FROM Cita WHERE doctor_id = ? ORDER BY fechaHora DESC";
        List<Cita> out = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setInt(1, doctorId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    Doctor   d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();
                    out.add(new Cita(rs.getInt("id"), p, d, fh));
                }
            }
        }
        return out;
    }
}
