package com.biblioteca.dao;

import com.biblioteca.model.Autor;
import com.biblioteca.util.DatabaseConnection;
import com.biblioteca.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutorDAOImpl implements AutorDAO {
    private static final Logger logger = LoggerUtil.getLogger(AutorDAOImpl.class);
    private final Connection connection;

    public AutorDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Optional<Autor> findById(int id) {
        String sql = "SELECT * FROM autores WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToAutor(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar autor por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Autor> findAll() {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT * FROM autores";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                autores.add(mapResultSetToAutor(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los autores", e);
        }
        return autores;
    }

    @Override
    public void save(Autor autor) {
        String sql = "INSERT INTO autores (nombre, nacionalidad) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, autor.getNombre());
            statement.setString(2, autor.getNacionalidad());
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    autor.setId(generatedKeys.getInt(1));
                }
            }
            logger.info("Autor guardado: " + autor);
        } catch (SQLException e) {
            logger.error("Error al guardar autor", e);
        }
    }

    @Override
    public void update(Autor autor) {
        String sql = "UPDATE autores SET nombre = ?, nacionalidad = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, autor.getNombre());
            statement.setString(2, autor.getNacionalidad());
            statement.setInt(3, autor.getId());
            statement.executeUpdate();
            logger.info("Autor actualizado: " + autor);
        } catch (SQLException e) {
            logger.error("Error al actualizar autor", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM autores WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            logger.info("Autor eliminado con ID: " + id);
        } catch (SQLException e) {
            logger.error("Error al eliminar autor con ID: " + id, e);
        }
    }

    @Override
    public List<Autor> findByNacionalidad(String nacionalidad) {
        List<Autor> autores = new ArrayList<>();
        String sql = "SELECT * FROM autores WHERE nacionalidad = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nacionalidad);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                autores.add(mapResultSetToAutor(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar autores por nacionalidad: " + nacionalidad, e);
        }
        return autores;
    }

    private Autor mapResultSetToAutor(ResultSet resultSet) throws SQLException {
        Autor autor = new Autor();
        autor.setId(resultSet.getInt("id"));
        autor.setNombre(resultSet.getString("nombre"));
        autor.setNacionalidad(resultSet.getString("nacionalidad"));
        return autor;
    }
}