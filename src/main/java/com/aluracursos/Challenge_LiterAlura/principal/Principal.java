package com.aluracursos.Challenge_LiterAlura.principal;

import com.aluracursos.Challenge_LiterAlura.model.Autor;
import com.aluracursos.Challenge_LiterAlura.model.Datos;
import com.aluracursos.Challenge_LiterAlura.model.DatosLibro;
import com.aluracursos.Challenge_LiterAlura.model.Libro;
import com.aluracursos.Challenge_LiterAlura.repository.AutorRepository;
import com.aluracursos.Challenge_LiterAlura.repository.LibroRepository;
import com.aluracursos.Challenge_LiterAlura.service.ConsumoAPI;
import com.aluracursos.Challenge_LiterAlura.service.ConvierteDatos;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL = "https://gutendex.com/books/";
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Integer opcion = 10;
    private Scanner scanner = new Scanner(System.in);
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    private void leerLibro(Libro libro) {
        System.out.printf("""
                        ----- LIBRO -----
                        Titulo: %s
                        Autor: %s
                        Idioma: %s
                        Numero de descargas: %d
                        -------------------- \n
                        """,
                libro.getTitulo(),
                libro.getAutor().getNombre(),
                libro.getIdioma(),
                libro.getNumeroDeDescargas());
    }

    private void buscarLibro() {
        System.out.println("Ingresa el nombre del libro que deseas buscar:");
        scanner.nextLine();
        String nombreLibro = scanner.nextLine();
        String json = consumoApi.obtenerLibros
                (URL + "?search=" + nombreLibro.replace(" ", "+"));
        List<DatosLibro> libros = convierteDatos.obtenerDatos(json, Datos.class).resultados();
        Optional<DatosLibro> libroOptional = libros.stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                .findFirst();
        if (libroOptional.isPresent()) {
            var libro = new Libro(libroOptional.get());
            libroRepository.save(libro);
            leerLibro(libro);
        }
        System.out.println("El libro no se ha encontrado");
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        libros.stream()
                .forEach(this::leerLibro);
    }


    private void leerAutor(Autor autor) {
        System.out.printf("""
                        Autor: %s
                        Fecha de nacimiento: %s
                        Fecha de fallecimiento: %s
                        """,
                autor.getNombre(),
                autor.getFechaDeNacimiento(),
                autor.getFechaDeFallecimiento());

        var libros = autor.getLibros().stream()
                .map(a -> a.getTitulo())
                .collect(Collectors.toList());
        System.out.println("Libros: " + libros + "\n");
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        autores.stream()
                .forEach(this::leerAutor);
    }

    private void listarAutoresPorAño() {
        System.out.println("Ingresa el año en que vivio el autor que desea buscar");
        Integer año = scanner.nextInt();
        List<Autor> autores = autorRepository.findByFechaDeFallecimientoGreaterThan(año);
        autores.stream()
                .forEach(this::leerAutor);
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingrese el idioma para buscar los libros
                es - español
                en - ingles
                fr - frances
                pt - portugues
                """);
        String idioma = scanner.next();
        List<Libro> libros = libroRepository.findByIdioma(idioma);
        libros.stream()
                .forEach(this::leerLibro);
    }

    private void generarEstadisticasDelNumeroDeDescargas() {
        var libros = libroRepository.findAll();
        DoubleSummaryStatistics doubleSummaryStatistics = new DoubleSummaryStatistics();
        for (Libro libro : libros) doubleSummaryStatistics.accept(libro.getNumeroDeDescargas());
        System.out.println("Conteo del numero de descargas - " + doubleSummaryStatistics.getCount());
        System.out.println("Numero de descargas minimo - " + doubleSummaryStatistics.getMin());
        System.out.println("Numero de descargas maximo - " + doubleSummaryStatistics.getMax());
        System.out.println("Suma del numero de descargas - " + doubleSummaryStatistics.getSum());
        System.out.println("Promedio del numero de descargas - " + doubleSummaryStatistics.getAverage() + "\n");
    }

    private void listarTop10Libros() {
        libroRepository.buscarTop10Libros().stream()
                .forEach(this::leerLibro);
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingresa el nombre del autor que buscas");
        scanner.nextLine();
        var nombre = scanner.nextLine();
        autorRepository.findByNombre(nombre).stream()
                .forEach(this::leerAutor);
    }

    public void mostrarMenu() {
        var option =-1;
        while (option != 9) {
            var menu= """
                    Elija la opcion a traves de su numero:
                    1- Buscar libro por titulo
                    2- Mostrar libros registrados
                    3- Mostrar autores registrados
                    4- Mostrar autores vivos en un determinado año
                    5- Mostrar libros por idioma
                    6- Generar estadisticas del numero de descargas
                    7- Mostrar el top 10 de libros mas descargados
                    8- Buscar autor por nombre
                    9- salir
                    """;
            System.out.println(menu);
            option = scanner.nextInt();
            switch (option) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresPorAño();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    generarEstadisticasDelNumeroDeDescargas();
                    break;
                case 7:
                    listarTop10Libros();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 9:
                    System.out.println("Cerrando la aplicacion...");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }
    }
}
