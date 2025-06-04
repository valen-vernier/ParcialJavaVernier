package com.biblioteca.dao;

import com.biblioteca.model.Libro;
import java.time.LocalDate;
import java.util.List;

public interface LibroDAO extends GenericDAO<Libro> {
    List<Libro> findByTitulo(String titulo);
    List<Libro> findByFechaPublicacionAfter(LocalDate fecha);
    List<Libro> findByAutorId(int autorId);
}