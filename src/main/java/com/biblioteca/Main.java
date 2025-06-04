package com.biblioteca;

import com.biblioteca.dao.AutorDAO;
import com.biblioteca.dao.AutorDAOImpl;
import com.biblioteca.dao.LibroDAO;
import com.biblioteca.dao.LibroDAOImpl;
import com.biblioteca.model.Autor;
import com.biblioteca.model.Libro;
import com.biblioteca.util.DatabaseConnection;
import com.biblioteca.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerUtil.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final AutorDAO autorDAO = new AutorDAOImpl();
    private static final LibroDAO libroDAO = new LibroDAOImpl();

    public static void main(String[] args) {
        logger.info("Iniciando aplicación de gestión de biblioteca");
        
        try {
            int opcion;
            do {
                mostrarMenuPrincipal();
                opcion = leerEntero("Seleccione una opción: ");
                
                switch (opcion) {
                    case 1:
                        gestionarAutores();
                        break;
                    case 2:
                        gestionarLibros();
                        break;
                    case 3:
                        buscarLibrosPorAutor();
                        break;
                    case 4:
                        buscarAutoresPorNacionalidad();
                        break;
                    case 5:
                        buscarLibrosRecientes();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } while (opcion != 0);
        } finally {
            DatabaseConnection.closeConnection();
            scanner.close();
            logger.info("Aplicación finalizada");
        }
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Gestión de Autores");
        System.out.println("2. Gestión de Libros");
        System.out.println("3. Buscar libros por autor");
        System.out.println("4. Buscar autores por nacionalidad");
        System.out.println("5. Buscar libros recientes");
        System.out.println("0. Salir");
    }

    private static void gestionarAutores() {
        int opcion;
        do {
            System.out.println("\n=== GESTIÓN DE AUTORES ===");
            System.out.println("1. Listar todos los autores");
            System.out.println("2. Buscar autor por ID");
            System.out.println("3. Agregar nuevo autor");
            System.out.println("4. Actualizar autor");
            System.out.println("5. Eliminar autor");
            System.out.println("0. Volver al menú principal");
            
            opcion = leerEntero("Seleccione una opción: ");
            
            switch (opcion) {
                case 1:
                    listarAutores();
                    break;
                case 2:
                    buscarAutorPorId();
                    break;
                case 3:
                    agregarAutor();
                    break;
                case 4:
                    actualizarAutor();
                    break;
                case 5:
                    eliminarAutor();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    private static void gestionarLibros() {
        int opcion;
        do {
            System.out.println("\n=== GESTIÓN DE LIBROS ===");
            System.out.println("1. Listar todos los libros");
            System.out.println("2. Buscar libro por ID");
            System.out.println("3. Buscar libros por título");
            System.out.println("4. Agregar nuevo libro");
            System.out.println("5. Actualizar libro");
            System.out.println("6. Eliminar libro");
            System.out.println("0. Volver al menú principal");
            
            opcion = leerEntero("Seleccione una opción: ");
            
            switch (opcion) {
                case 1:
                    listarLibros();
                    break;
                case 2:
                    buscarLibroPorId();
                    break;
                case 3:
                    buscarLibrosPorTitulo();
                    break;
                case 4:
                    agregarLibro();
                    break;
                case 5:
                    actualizarLibro();
                    break;
                case 6:
                    eliminarLibro();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    private static void listarAutores() {
        System.out.println("\n=== LISTA DE AUTORES ===");
        List<Autor> autores = autorDAO.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private static void buscarAutorPorId() {
        int id = leerEntero("Ingrese el ID del autor: ");
        var autor = autorDAO.findById(id);
        if (autor.isPresent()) {
            System.out.println("Autor encontrado: " + autor.get());
        } else {
            System.out.println("No se encontró un autor con ID: " + id);
        }
    }

    private static void agregarAutor() {
        System.out.println("\n=== AGREGAR NUEVO AUTOR ===");
        String nombre = leerCadena("Nombre del autor: ");
        String nacionalidad = leerCadena("Nacionalidad: ");
        
        Autor autor = new Autor(nombre, nacionalidad);
        autorDAO.save(autor);
        System.out.println("Autor agregado exitosamente con ID: " + autor.getId());
    }

    private static void actualizarAutor() {
        System.out.println("\n=== ACTUALIZAR AUTOR ===");
        int id = leerEntero("Ingrese el ID del autor a actualizar: ");
        var autorOpt = autorDAO.findById(id);
        
        if (autorOpt.isPresent()) {
            Autor autor = autorOpt.get();
            System.out.println("Autor actual: " + autor);
            
            String nombre = leerCadena("Nuevo nombre (dejar vacío para no cambiar): ");
            if (!nombre.isEmpty()) {
                autor.setNombre(nombre);
            }
            
            String nacionalidad = leerCadena("Nueva nacionalidad (dejar vacío para no cambiar): ");
            if (!nacionalidad.isEmpty()) {
                autor.setNacionalidad(nacionalidad);
            }
            
            autorDAO.update(autor);
            System.out.println("Autor actualizado exitosamente.");
        } else {
            System.out.println("No se encontró un autor con ID: " + id);
        }
    }

    private static void eliminarAutor() {
        System.out.println("\n=== ELIMINAR AUTOR ===");
        int id = leerEntero("Ingrese el ID del autor a eliminar: ");
        
        // Primero verificamos si el autor existe
        var autorOpt = autorDAO.findById(id);
        if (autorOpt.isPresent()) {
            // Verificamos si el autor tiene libros asociados
            List<Libro> libros = libroDAO.findByAutorId(id);
            if (!libros.isEmpty()) {
                System.out.println("No se puede eliminar el autor porque tiene libros asociados.");
                System.out.println("Libros asociados:");
                libros.forEach(System.out::println);
                return;
            }
            
            autorDAO.delete(id);
            System.out.println("Autor eliminado exitosamente.");
        } else {
            System.out.println("No se encontró un autor con ID: " + id);
        }
    }

    private static void listarLibros() {
        System.out.println("\n=== LISTA DE LIBROS ===");
        List<Libro> libros = libroDAO.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private static void buscarLibroPorId() {
        int id = leerEntero("Ingrese el ID del libro: ");
        var libro = libroDAO.findById(id);
        if (libro.isPresent()) {
            System.out.println("Libro encontrado: " + libro.get());
        } else {
            System.out.println("No se encontró un libro con ID: " + id);
        }
    }

    private static void buscarLibrosPorTitulo() {
        String titulo = leerCadena("Ingrese parte del título a buscar: ");
        List<Libro> libros = libroDAO.findByTitulo(titulo);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros con título que contenga: " + titulo);
        } else {
            System.out.println("Libros encontrados:");
            libros.forEach(System.out::println);
        }
    }

    private static void agregarLibro() {
        System.out.println("\n=== AGREGAR NUEVO LIBRO ===");
        
        // Mostrar lista de autores para referencia
        System.out.println("Autores disponibles:");
        List<Autor> autores = autorDAO.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados. Debe registrar un autor primero.");
            return;
        }
        autores.forEach(a -> System.out.println(a.getId() + ": " + a.getNombre()));
        
        String titulo = leerCadena("Título del libro: ");
        String isbn = leerCadena("ISBN: ");
        LocalDate fechaPublicacion = leerFecha("Fecha de publicación (YYYY-MM-DD): ");
        int autorId = leerEntero("ID del autor: ");
        
        // Verificar que el autor existe
        if (autorDAO.findById(autorId).isEmpty()) {
            System.out.println("No existe un autor con ID: " + autorId);
            return;
        }
        
        Libro libro = new Libro(titulo, isbn, fechaPublicacion, autorId);
        libroDAO.save(libro);
        System.out.println("Libro agregado exitosamente con ID: " + libro.getId());
    }

    private static void actualizarLibro() {
        System.out.println("\n=== ACTUALIZAR LIBRO ===");
        int id = leerEntero("Ingrese el ID del libro a actualizar: ");
        var libroOpt = libroDAO.findById(id);
        
        if (libroOpt.isPresent()) {
            Libro libro = libroOpt.get();
            System.out.println("Libro actual: " + libro);
            
            String titulo = leerCadena("Nuevo título (dejar vacío para no cambiar): ");
            if (!titulo.isEmpty()) {
                libro.setTitulo(titulo);
            }
            
            String isbn = leerCadena("Nuevo ISBN (dejar vacío para no cambiar): ");
            if (!isbn.isEmpty()) {
                libro.setIsbn(isbn);
            }
            
            String fechaStr = leerCadena("Nueva fecha de publicación (YYYY-MM-DD, dejar vacío para no cambiar): ");
            if (!fechaStr.isEmpty()) {
                libro.setFechaPublicacion(LocalDate.parse(fechaStr));
            }
            
            String autorIdStr = leerCadena("Nuevo ID de autor (dejar vacío para no cambiar): ");
            if (!autorIdStr.isEmpty()) {
                int autorId = Integer.parseInt(autorIdStr);
                // Verificar que el nuevo autor existe
                if (autorDAO.findById(autorId).isEmpty()) {
                    System.out.println("No existe un autor con ID: " + autorId);
                    return;
                }
                libro.setAutorId(autorId);
            }
            
            libroDAO.update(libro);
            System.out.println("Libro actualizado exitosamente.");
        } else {
            System.out.println("No se encontró un libro con ID: " + id);
        }
    }

    private static void eliminarLibro() {
        System.out.println("\n=== ELIMINAR LIBRO ===");
        int id = leerEntero("Ingrese el ID del libro a eliminar: ");
        
        // Verificamos si el libro existe
        var libroOpt = libroDAO.findById(id);
        if (libroOpt.isPresent()) {
            libroDAO.delete(id);
            System.out.println("Libro eliminado exitosamente.");
        } else {
            System.out.println("No se encontró un libro con ID: " + id);
        }
    }

    private static void buscarLibrosPorAutor() {
        System.out.println("\n=== BUSCAR LIBROS POR AUTOR ===");
        
        // Mostrar lista de autores para referencia
        System.out.println("Autores disponibles:");
        List<Autor> autores = autorDAO.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }
        autores.forEach(a -> System.out.println(a.getId() + ": " + a.getNombre()));
        
        int autorId = leerEntero("Ingrese el ID del autor: ");
        List<Libro> libros = libroDAO.findByAutorId(autorId);
        
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros para el autor con ID: " + autorId);
        } else {
            System.out.println("Libros encontrados:");
            libros.forEach(System.out::println);
        }
    }

    private static void buscarAutoresPorNacionalidad() {
        System.out.println("\n=== BUSCAR AUTORES POR NACIONALIDAD ===");
        String nacionalidad = leerCadena("Ingrese la nacionalidad a buscar: ");
        List<Autor> autores = autorDAO.findByNacionalidad(nacionalidad);
        
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores de nacionalidad: " + nacionalidad);
        } else {
            System.out.println("Autores encontrados:");
            autores.forEach(System.out::println);
        }
    }

    private static void buscarLibrosRecientes() {
        System.out.println("\n=== BUSCAR LIBROS RECIENTES ===");
        int años = leerEntero("Ingrese el número de años hacia atrás para buscar: ");
        LocalDate fechaLimite = LocalDate.now().minusYears(años);
        
        List<Libro> libros = libroDAO.findByFechaPublicacionAfter(fechaLimite);
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros publicados después de: " + fechaLimite);
        } else {
            System.out.println("Libros encontrados publicados después de " + fechaLimite + ":");
            libros.forEach(System.out::println);
        }
    }

    // Métodos auxiliares para entrada de datos
    private static String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }
    }

    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Use YYYY-MM-DD.");
            }
        }
    }
}