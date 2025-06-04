package com.biblioteca.dao;

import com.biblioteca.model.Libro;
import com.biblioteca.util.DatabaseConnection;
import com.biblioteca.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroDAOImpl implements LibroDAO {
    private static final Logger logger = LoggerUtil.getLogger(LibroDAOImpl.class);
    private final Connection connection;

    public LibroDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Libro> findById(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToLibro(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar libro por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Libro> findAll() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                libros.add(mapResultSetToLibro(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los libros", e);
        }
        return libros;
    }

    @Override
    public void save(Libro libro) {
        String sql = "INSERT INTO libros (titulo, isbn, fecha_publicacion, autor_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getIsbn());
            statement.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            statement.setInt(4, libro.getAutorId());
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    libro.setId(generatedKeys.getInt(1));
                }
            }
            logger.info("Libro guardado: " + libro);
        } catch (SQLException e) {
            logger.error("Error al guardar libro", e);
        }
    }

    @Override
    public void update(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, isbn = ?, fecha_publicacion = ?, autor_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getIsbn());
            statement.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            statement.setInt(4, libro.getAutorId());
            statement.setInt(5, libro.getId());
            statement.executeUpdate();
            logger.info("Libro actualizado: " + libro);
        } catch (SQLException e) {
            logger.error("Error al actualizar libro", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            logger.info("Libro eliminado con ID: " + id);
        } catch (SQLException e) {
            logger.error("Error al eliminar libro con ID: " + id, e);
        }
    }

    @Override
    public List<Libro> findByTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + titulo + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                libros.add(mapResultSetToLibro(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar libros por título: " + titulo, e);
        }
        return libros;
    }

    @Override
    public List<Libro> findByFechaPublicacionAfter(LocalDate fecha) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE fecha_publicacion > ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(fecha));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                libros.add(mapResultSetToLibro(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar libros después de la fecha: " + fecha, e);
        }
        return libros;
    }

    @Override
    public List<Libro> findByAutorId(int autorId) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE autor_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, autorId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                libros.add(mapResultSetToLibro(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar libros por autor ID: " + autorId, e);
        }
        return libros;
    }

    private Libro mapResultSetToLibro(ResultSet resultSet) throws SQLException {
        Libro libro = new Libro();
        libro.setId(resultSet.getInt("id"));
        libro.setTitulo(resultSet.getString("titulo"));
        libro.setIsbn(resultSet.getString("isbn"));
        libro.setFechaPublicacion(resultSet.getDate("fecha_publicacion").toLocalDate());
        libro.setAutorId(resultSet.getInt("autor_id"));
        return libro;
    }
}