package com.biblioteca.dao;

import com.biblioteca.model.Autor;
import java.util.List;

public interface AutorDAO extends GenericDAO<Autor> {
    List<Autor> findByNacionalidad(String nacionalidad);
}