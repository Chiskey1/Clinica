package dao;

import Modelo.Enfermero;
import Modelo.HojaEnfermeria;
import Modelo.Paciente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HojaEnfermeriaDAO {

    private final EnfermeroDAO enfermeroDAO = new EnfermeroDAO();  // DAO para obtener info de enfermeros
    private final PacienteDAO pacienteDAO   = new PacienteDAO();    // DAO para obtener info de pacientes

    /**
     * Inserta una nueva hoja de enfermería en la BD.
     * Devuelve el ID generado para el nuevo registro.
     */
    public int insertar(HojaEnfermeria h) throws SQLException {
        String sql = """
            INSERT INTO HojaEnfermeria (fechaHora, signosVitales, observaciones, enfermero_id, paciente_id)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setTimestamp(1, Timestamp.valueOf(h.getFechaHora())); // Convertir LocalDateTime a Timestamp
            st.setString(2, h.getSignosVitales());
            st.setString(3, h.getObservaciones());
            st.setInt(4, h.getEnfermero().getId());
            st.setInt(5, h.getPaciente().getId());
            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    h.setId(id);  // asignar ID al objeto
                    return id;
                }
            }
        }
        throw new SQLException("No se generó ID para HojaEnfermeria");
    }

    /**
     * Lista todas las hojas de enfermería ordenadas por fecha (más recientes primero).
     * Cada hoja trae el enfermero y paciente relacionados.
     */
    public List<HojaEnfermeria> listar() throws SQLException {
        String sql = "SELECT * FROM HojaEnfermeria ORDER BY fechaHora DESC";
        List<HojaEnfermeria> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Enfermero e = enfermeroDAO.buscarPorId(rs.getInt("enfermero_id"));
                Paciente  p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                // Crear objeto con todos los datos
                HojaEnfermeria h = new HojaEnfermeria(
                        rs.getInt("id"),
                        fh,
                        rs.getString("signosVitales"),
                        rs.getString("observaciones"),
                        e, p
                );
                out.add(h);
            }
        }
        return out;
    }

    /**
     * Lista hojas de enfermería de un paciente específico, ordenadas por fecha.
     */
    public List<HojaEnfermeria> listarPorPaciente(int pacienteId) throws SQLException {
        String sql = "SELECT * FROM HojaEnfermeria WHERE paciente_id = ? ORDER BY fechaHora DESC";
        List<HojaEnfermeria> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, pacienteId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Enfermero e = enfermeroDAO.buscarPorId(rs.getInt("enfermero_id"));
                    Paciente  p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                    HojaEnfermeria h = new HojaEnfermeria(
                            rs.getInt("id"),
                            fh,
                            rs.getString("signosVitales"),
                            rs.getString("observaciones"),
                            e, p
                    );
                    out.add(h);
                }
            }
        }
        return out;
    }

    /**
     * Lista hojas de enfermería hechas por un enfermero específico, ordenadas por fecha.
     */
    public List<HojaEnfermeria> listarPorEnfermero(int enfermeroId) throws SQLException {
        String sql = "SELECT * FROM HojaEnfermeria WHERE enfermero_id = ? ORDER BY fechaHora DESC";
        List<HojaEnfermeria> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, enfermeroId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Enfermero e = enfermeroDAO.buscarPorId(rs.getInt("enfermero_id"));
                    Paciente  p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                    HojaEnfermeria h = new HojaEnfermeria(
                            rs.getInt("id"),
                            fh,
                            rs.getString("signosVitales"),
                            rs.getString("observaciones"),
                            e, p
                    );
                    out.add(h);
                }
            }
        }
        return out;
    }

    /**
     * Busca una hoja de enfermería por su ID, devuelve null si no existe.
     */
    public HojaEnfermeria buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM HojaEnfermeria WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Enfermero e = enfermeroDAO.buscarPorId(rs.getInt("enfermero_id"));
                    Paciente  p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDateTime fh = rs.getTimestamp("fechaHora").toLocalDateTime();

                    return new HojaEnfermeria(
                            rs.getInt("id"),
                            fh,
                            rs.getString("signosVitales"),
                            rs.getString("observaciones"),
                            e, p
                    );
                }
            }
        }
        return null;
    }

    /**
     * Elimina una hoja de enfermería por su ID.
     * Lanza excepción si el ID no existe.
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM HojaEnfermeria WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            int affected = st.executeUpdate();
            if (affected == 0) {
                throw new SQLException("No se encontró hoja de enfermería con ID: " + id);
            }
        }
    }
}
