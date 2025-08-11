package dao;

import Modelo.Enfermero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnfermeroDAO {

    /**
     * Inserta un nuevo enfermero en la base de datos.
     * Primero inserta en Persona y luego en Enfermero (usa transacción para mantener integridad).
     * Devuelve el id generado.
     */
    public int insertar(Enfermero e) throws SQLException {
        String sqlPersona = "INSERT INTO Persona (nombre, telefono, sexo, domicilio, email) VALUES (?,?,?,?,?)";
        String sqlEnf     = "INSERT INTO Enfermero (id) VALUES (?)";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false); // iniciar transacción
            try (PreparedStatement st = c.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                // Insertar datos en tabla Persona
                st.setString(1, e.getNombre());
                st.setString(2, e.getTelefono());
                st.setString(3, e.getSexo());
                st.setString(4, e.getDomicilio());
                st.setString(5, e.getEmail());
                st.executeUpdate();

                // Obtener ID generado para Persona
                int idPersona;
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se generó ID para Persona (Enfermero)");
                    idPersona = rs.getInt(1);
                }

                // Insertar el id en tabla Enfermero
                try (PreparedStatement st2 = c.prepareStatement(sqlEnf)) {
                    st2.setInt(1, idPersona);
                    st2.executeUpdate();
                }

                c.commit();  // confirmar transacción
                e.setId(idPersona);  // asignar id al objeto
                return idPersona;

            } catch (SQLException ex) {
                c.rollback();  // revertir si falla
                throw ex;
            } finally {
                c.setAutoCommit(true); // restaurar modo automático
            }
        }
    }

    /**
     * Elimina un enfermero y su persona asociada dentro de una transacción.
     * Asegura que ambas tablas queden sincronizadas.
     */
    public void eliminar(int id) throws SQLException {
        String sqlEnfermero = "DELETE FROM Enfermero WHERE id = ?";
        String sqlPersona   = "DELETE FROM Persona WHERE id = ?";

        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false); // iniciar transacción
            try (PreparedStatement stEnf = c.prepareStatement(sqlEnfermero);
                 PreparedStatement stPer = c.prepareStatement(sqlPersona)) {

                // Eliminar primero en Enfermero
                stEnf.setInt(1, id);
                stEnf.executeUpdate();

                // Luego eliminar en Persona
                stPer.setInt(1, id);
                stPer.executeUpdate();

                c.commit();  // confirmar cambios
            } catch (SQLException ex) {
                c.rollback();  // revertir si hay error
                throw ex;
            } finally {
                c.setAutoCommit(true); // restaurar modo automático
            }
        }
    }

    /**
     * Lista todos los enfermeros con sus datos completos.
     * Realiza join con Persona para obtener la información general.
     */
    public List<Enfermero> listar() throws SQLException {
        String sql = """
            SELECT en.id, pe.nombre, pe.telefono, pe.sexo, pe.domicilio, pe.email
            FROM Enfermero en
            JOIN Persona pe ON en.id = pe.id
            ORDER BY pe.nombre
        """;
        List<Enfermero> out = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Enfermero e = new Enfermero(
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("sexo"),
                        rs.getString("domicilio"),
                        rs.getString("email")
                );
                e.setId(rs.getInt("id"));
                out.add(e);
            }
        }
        return out;
    }

    /**
     * Busca un enfermero por su id, devuelve null si no existe.
     */
    public Enfermero buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT en.id, pe.nombre, pe.telefono, pe.sexo, pe.domicilio, pe.email
            FROM Enfermero en
            JOIN Persona pe ON en.id = pe.id
            WHERE en.id = ?
        """;
        try (Connection c = DBUtil.getConnection();
             PreparedStatement st = c.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Enfermero e = new Enfermero(
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("sexo"),
                            rs.getString("domicilio"),
                            rs.getString("email")
                    );
                    e.setId(rs.getInt("id"));
                    return e;
                }
            }
        }
        return null;
    }
}
