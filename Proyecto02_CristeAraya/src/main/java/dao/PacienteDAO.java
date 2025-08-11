package dao;

import Modelo.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    /**
     * Inserta un nuevo paciente en las tablas Persona y Paciente.
     * Se usa transacción para asegurar consistencia.
     * Retorna el ID generado para el nuevo paciente.
     */
    public int insertar(Paciente p) throws SQLException {
        String sqlPersona  = "INSERT INTO Persona (nombre, telefono, sexo, domicilio, email) VALUES (?,?,?,?,?)";
        String sqlPaciente = "INSERT INTO Paciente (id) VALUES (?)";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement st = c.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {

                st.setString(1, p.getNombre());
                st.setString(2, p.getTelefono());
                st.setString(3, p.getSexo());
                st.setString(4, p.getDomicilio());
                st.setString(5, p.getEmail());
                st.executeUpdate();

                int idPersona;
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se generó ID para Persona");
                    idPersona = rs.getInt(1);
                }

                try (PreparedStatement st2 = c.prepareStatement(sqlPaciente)) {
                    st2.setInt(1, idPersona);
                    st2.executeUpdate();
                }

                c.commit();
                p.setId(idPersona);
                return idPersona;

            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    /**
     * Obtiene la lista completa de pacientes (con datos de Persona).
     */
    public List<Paciente> listar() throws SQLException {
        String sql = """
            SELECT pe.id, pe.nombre, pe.telefono, pe.sexo, pe.domicilio, pe.email
            FROM Paciente pa
            JOIN Persona pe ON pa.id = pe.id
            ORDER BY pe.nombre
        """;

        List<Paciente> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Paciente p = new Paciente(
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("sexo"),
                        rs.getString("domicilio"),
                        rs.getString("email")
                );
                p.setId(rs.getInt("id"));
                out.add(p);
            }
        }
        return out;
    }

    /**
     * Busca un paciente por su email.
     * Retorna null si no existe.
     */
    public Paciente buscarPorEmail(String email) throws SQLException {
        String sql = """
            SELECT pe.id, pe.nombre, pe.telefono, pe.sexo, pe.domicilio, pe.email
            FROM Paciente pa
            JOIN Persona pe ON pa.id = pe.id
            WHERE pe.email = ?
        """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {

            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Paciente p = new Paciente(
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("sexo"),
                            rs.getString("domicilio"),
                            rs.getString("email")
                    );
                    p.setId(rs.getInt("id"));
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Busca un paciente por su ID.
     * Retorna null si no existe.
     */
    public Paciente buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT pe.id, pe.nombre, pe.telefono, pe.sexo, pe.domicilio, pe.email
            FROM Paciente pa
            JOIN Persona pe ON pa.id = pe.id
            WHERE pe.id = ?
        """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Paciente p = new Paciente(
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("sexo"),
                            rs.getString("domicilio"),
                            rs.getString("email")
                    );
                    p.setId(rs.getInt("id"));
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * Elimina un paciente de ambas tablas (Paciente y Persona).
     * Usa transacción para asegurar consistencia.
     */
    public void eliminar(int id) throws SQLException {
        String sqlPaciente = "DELETE FROM Paciente WHERE id = ?";
        String sqlPersona = "DELETE FROM Persona WHERE id = ?";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement stPaciente = c.prepareStatement(sqlPaciente);
                 PreparedStatement stPersona = c.prepareStatement(sqlPersona)) {

                stPaciente.setInt(1, id);
                stPaciente.executeUpdate();

                stPersona.setInt(1, id);
                stPersona.executeUpdate();

                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}
