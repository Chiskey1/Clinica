package dao;

import Modelo.Doctor;
import Modelo.HojaMedica;
import Modelo.Paciente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HojaMedicaDAO {

    private final DoctorDAO doctorDAO = new DoctorDAO();    // DAO para acceder a datos de Doctor
    private final PacienteDAO pacienteDAO = new PacienteDAO();  // DAO para acceder a datos de Paciente

    /**
     * Inserta una nueva Hoja Médica en la base de datos.
     * Retorna el ID generado para la nueva hoja.
     */
    public int insertar(HojaMedica h) throws SQLException {
        String sql = """
            INSERT INTO HojaMedica (fechaHora, motivoConsulta, diagnosticos, tratamiento, doctor_id, paciente_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setTimestamp(1, Timestamp.valueOf(h.getFechaHora()));  // Convierte LocalDateTime a Timestamp
            st.setString(2, h.getMotivoConsulta());
            st.setString(3, h.getDiagnosticos());
            st.setString(4, h.getTratamiento());
            st.setInt(5, h.getDoctor().getId());
            st.setInt(6, h.getPaciente().getId());
            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    h.setId(id);  // Asigna el ID generado al objeto
                    return id;
                }
            }
        }
        throw new SQLException("No se generó ID para HojaMedica");
    }

    /**
     * Lista todas las hojas médicas, ordenadas por fecha descendente.
     * Carga los objetos Doctor y Paciente relacionados.
     */
    public List<HojaMedica> listar() throws SQLException {
        String sql = "SELECT * FROM HojaMedica ORDER BY fechaHora DESC";
        List<HojaMedica> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Doctor d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                HojaMedica h = new HojaMedica(
                        rs.getInt("id"),
                        fh,
                        rs.getString("motivoConsulta"),
                        rs.getString("diagnosticos"),
                        rs.getString("tratamiento"),
                        d, p
                );
                out.add(h);
            }
        }
        return out;
    }

    /**
     * Lista las hojas médicas asociadas a un paciente específico.
     */
    public List<HojaMedica> listarPorPaciente(int pacienteId) throws SQLException {
        String sql = "SELECT * FROM HojaMedica WHERE paciente_id = ? ORDER BY fechaHora DESC";
        List<HojaMedica> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, pacienteId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Doctor d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                    HojaMedica h = new HojaMedica(
                            rs.getInt("id"),
                            fh,
                            rs.getString("motivoConsulta"),
                            rs.getString("diagnosticos"),
                            rs.getString("tratamiento"),
                            d, p
                    );
                    out.add(h);
                }
            }
        }
        return out;
    }

    /**
     * Lista las hojas médicas realizadas por un doctor específico.
     */
    public List<HojaMedica> listarPorDoctor(int doctorId) throws SQLException {
        String sql = "SELECT * FROM HojaMedica WHERE doctor_id = ? ORDER BY fechaHora DESC";
        List<HojaMedica> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, doctorId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Doctor d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                    HojaMedica h = new HojaMedica(
                            rs.getInt("id"),
                            fh,
                            rs.getString("motivoConsulta"),
                            rs.getString("diagnosticos"),
                            rs.getString("tratamiento"),
                            d, p
                    );
                    out.add(h);
                }
            }
        }
        return out;
    }

    /**
     * Busca una hoja médica por su ID.
     * Retorna null si no se encuentra.
     */
    public HojaMedica buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM HojaMedica WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Doctor d = doctorDAO.buscarPorId(rs.getInt("doctor_id"));
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                    return new HojaMedica(
                            rs.getInt("id"),
                            fh,
                            rs.getString("motivoConsulta"),
                            rs.getString("diagnosticos"),
                            rs.getString("tratamiento"),
                            d, p
                    );
                }
            }
        }
        return null;
    }

    /**
     * Elimina una hoja médica por su ID.
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM HojaMedica WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
        }
    }
}
