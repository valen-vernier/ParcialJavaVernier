package com.biblioteca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:h2:file:./database/biblioteca";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    private static Connection connection;
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                initializeDatabase();
            } catch (SQLException e) {
                throw new RuntimeException("Error al conectar con la base de datos", e);
            }
        }
        return connection;
    }
    
    private static void initializeDatabase() throws SQLException {
        try (var statement = connection.createStatement()) {
            // Crear tabla autores si no existe
            statement.execute("CREATE TABLE IF NOT EXISTS autores (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "nacionalidad VARCHAR(50))");
            
            // Crear tabla libros si no existe
            statement.execute("CREATE TABLE IF NOT EXISTS libros (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "titulo VARCHAR(100) NOT NULL, " +
                    "isbn VARCHAR(20) UNIQUE, " +
                    "fecha_publicacion DATE, " +
                    "autor_id INT, " +
                    "FOREIGN KEY (autor_id) REFERENCES autores(id))");
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}