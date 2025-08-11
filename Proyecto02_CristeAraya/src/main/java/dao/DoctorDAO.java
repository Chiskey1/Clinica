package dao;

import Modelo.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    /**
     * Inserta un doctor nuevo en la base de datos.
     * Primero inserta en Persona y luego en Doctor (usa transacción para asegurar integridad).
     * Devuelve el id generado para la persona/doctor.
     */
    public int insertar(Doctor d) throws SQLException {
        String sqlPersona = "INSERT INTO Persona (nombre, telefono, sexo, domicilio, email) VALUES (?,?,?,?,?)";
        String sqlDoctor  = "INSERT INTO Doctor (id, especialidad) VALUES (?, ?)";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false); // iniciar transacción
            try (PreparedStatement st = c.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                // Insertar datos en tabla Persona
                st.setString(1, d.getNombre());
                st.setString(2, d.getTelefono());
                st.setString(3, d.getSexo());
                st.setString(4, d.getDomicilio());
                st.setString(5, d.getEmail());
                st.executeUpdate();

                // Obtener ID generado para Persona
                int idPersona;
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se generó ID para Persona (Doctor)");
                    idPersona = rs.getInt(1);
                }

                // Insertar especialidad en tabla Doctor usando el mismo ID
                try (PreparedStatement st2 = c.prepareStatement(sqlDoctor)) {
                    st2.setInt(1, idPersona);
                    st2.setString(2, d.getEspecialidad());
                    st2.executeUpdate();
                }

                c.commit();  // confirmar transacción
                d.setId(idPersona); // asignar id al objeto
                return idPersona;

            } catch (SQLException ex) {
                c.rollback();  // revertir cambios en caso de error
                throw ex;
            } finally {
                c.setAutoCommit(true); // restaurar modo automático
            }
        }
    }

    /**
     * Lista todos los doctores con su información completa,
     * haciendo join con Persona para obtener datos generales.
     */
    public List<Doctor> listar() throws SQLException {
        String sql = """
            SELECT d.id, pe.nombre, pe.telefono, pe.sexo, pe.domicilio, pe.email, d.especialidad
            FROM Doctor d
            JOIN Persona pe ON d.id = pe.id
            ORDER BY pe.nombre
        """;
        List<Doctor> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Doctor d = new Doctor(
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("sexo"),
                        rs.getString("domicilio"),
                        rs.getString("email"),
                        rs.getString("especialidad")
                );
                d.setId(rs.getInt("id"));
                out.add(d);
            }
        }
        return out;
    }

    /**
     * Busca un doctor por su id, obteniendo toda su información.
     * Devuelve null si no lo encuentra.
     */
    public Doctor buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT d.id, pe.nombre, pe.telefono, pe.sexo, pe.domicilio, pe.email, d.especialidad
            FROM Doctor d
            JOIN Persona pe ON d.id = pe.id
            WHERE d.id = ?
        """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Doctor d = new Doctor(
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("sexo"),
                            rs.getString("domicilio"),
                            rs.getString("email"),
                            rs.getString("especialidad")
                    );
                    d.setId(rs.getInt("id"));
                    return d;
                }
            }
        }
        return null;
    }

    /**
     * Elimina un doctor y su persona asociada en transacción,
     * asegurando que ambas tablas queden sincronizadas.
     */
    public void eliminar(int id) throws SQLException {
        String sqlDoctor  = "DELETE FROM Doctor WHERE id = ?";
        String sqlPersona = "DELETE FROM Persona WHERE id = ?";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false); // iniciar transacción
            try (PreparedStatement stDoctor = c.prepareStatement(sqlDoctor);
                 PreparedStatement stPersona = c.prepareStatement(sqlPersona)) {

                // Elimina primero en Doctor
                stDoctor.setInt(1, id);
                stDoctor.executeUpdate();

                // Luego elimina en Persona
                stPersona.setInt(1, id);
                stPersona.executeUpdate();

                c.commit();  // confirmar transacción
            } catch (SQLException ex) {
                c.rollback();  // revertir si hay error
                throw ex;
            } finally {
                c.setAutoCommit(true); // restaurar modo automático
            }
        }
    }
}
