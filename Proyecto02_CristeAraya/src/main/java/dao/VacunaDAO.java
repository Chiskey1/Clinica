package dao;

import Modelo.Doctor;
import Modelo.Paciente;
import Modelo.Persona;
import Modelo.Vacuna;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VacunaDAO {

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final DoctorDAO doctorDAO = new DoctorDAO();
    private final EnfermeroDAO enfermeroDAO = new EnfermeroDAO();

    /**
     * Inserta una nueva vacuna en la base de datos.
     * @param v Objeto Vacuna con datos a insertar.
     * @return ID generado para la vacuna insertada.
     * @throws SQLException en caso de error SQL.
     */
    public int insertar(Vacuna v) throws SQLException {
        String sql = "INSERT INTO Vacuna (fecha, vacuna, aplicado_por_id, paciente_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setDate(1, Date.valueOf(v.getFecha())); // Convierte LocalDate a SQL Date
            st.setString(2, v.getVacuna());
            st.setInt(3, v.getAplicadoPor().getId());
            st.setInt(4, v.getPaciente().getId());
            st.executeUpdate();

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    v.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("No se generó ID para Vacuna");
    }

    /**
     * Lista todas las vacunas ordenadas por fecha descendente.
     */
    public List<Vacuna> listar() throws SQLException {
        String sql = "SELECT * FROM Vacuna ORDER BY fecha DESC";
        List<Vacuna> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Persona apl = findPersonaById(rs.getInt("aplicado_por_id"));
                Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                LocalDate f = rs.getDate("fecha").toLocalDate();
                Vacuna v = new Vacuna(
                        rs.getInt("id"),
                        f,
                        rs.getString("vacuna"),
                        apl,
                        p
                );
                out.add(v);
            }
        }
        return out;
    }

    /**
     * Lista vacunas filtradas por paciente.
     */
    public List<Vacuna> listarPorPaciente(int pacienteId) throws SQLException {
        String sql = "SELECT * FROM Vacuna WHERE paciente_id = ? ORDER BY fecha DESC";
        List<Vacuna> out = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, pacienteId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Persona apl = findPersonaById(rs.getInt("aplicado_por_id"));
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDate f = rs.getDate("fecha").toLocalDate();
                    Vacuna v = new Vacuna(
                            rs.getInt("id"),
                            f,
                            rs.getString("vacuna"),
                            apl,
                            p
                    );
                    out.add(v);
                }
            }
        }
        return out;
    }

    /**
     * Busca una vacuna por su ID.
     */
    public Vacuna buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Vacuna WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Persona apl = findPersonaById(rs.getInt("aplicado_por_id"));
                    Paciente p = pacienteDAO.buscarPorId(rs.getInt("paciente_id"));
                    LocalDate f = rs.getDate("fecha").toLocalDate();
                    return new Vacuna(
                            rs.getInt("id"),
                            f,
                            rs.getString("vacuna"),
                            apl,
                            p
                    );
                }
            }
        }
        return null;
    }

    /**
     * Busca una persona (Doctor o Enfermero) por ID.
     * Primero busca en DoctorDAO, si no lo encuentra busca en EnfermeroDAO.
     */
    private Persona findPersonaById(int id) throws SQLException {
        Doctor d = doctorDAO.buscarPorId(id);
        if (d != null) return d;
        return enfermeroDAO.buscarPorId(id);
    }

    /**
     * Elimina una vacuna por ID.
     * Lanza excepción si no existe la vacuna.
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Vacuna WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setInt(1, id);
            int affectedRows = st.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se encontró vacuna con ID: " + id);
            }
        }
    }
}
