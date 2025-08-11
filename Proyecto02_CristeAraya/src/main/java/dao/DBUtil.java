package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // URL de conexión a la base de datos SQL Server (localhost y base ExpedienteClinicaDB)
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=ExpedienteClinicaDB;encrypt=false";
    private static final String USER = "sa";          // Usuario para conexión a BD
    private static final String PASS = "Ina2025";      // Contraseña para conexión a BD

    // Bloque estático para cargar el driver de SQL Server al iniciar la clase
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            // Error si no se encuentra el driver JDBC necesario
            throw new RuntimeException("No se encontró el driver de SQL Server", e);
        }
    }

    // Método para obtener una conexión nueva a la base de datos
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
