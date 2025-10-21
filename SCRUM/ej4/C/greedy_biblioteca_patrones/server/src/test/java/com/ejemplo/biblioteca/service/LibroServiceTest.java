package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Domicilio;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.domain.TipoLibro;
import com.ejemplo.biblioteca.repository.AutorRepository;
import com.ejemplo.biblioteca.repository.LibroRepository;
import com.ejemplo.biblioteca.repository.LocalidadRepository;
import com.ejemplo.biblioteca.repository.PersonaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LibroServiceTest {

    @Autowired
    private LibroService libroService;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    private Autor autorA;
    private Autor autorB;
    private Persona personaA;
    private Persona personaB;

    @BeforeEach
    void setUp() {
        libroRepository.deleteAll();
        personaRepository.deleteAll();
        autorRepository.deleteAll();
        localidadRepository.deleteAll();

        autorA = autorRepository.save(buildAutor("Jorge", "Borges"));
        autorB = autorRepository.save(buildAutor("Julio", "Cortazar"));

        Localidad loc1 = localidadRepository.save(buildLocalidad("Buenos Aires"));
        Localidad loc2 = localidadRepository.save(buildLocalidad("Córdoba"));

        personaA = personaRepository.save(buildPersona("Ana", "Pérez", 12345678, "San Martín", 550, loc1));
        personaB = personaRepository.save(buildPersona("Luis", "Gómez", 87654321, "Belgrano", 1200, loc2));
    }

    @Test
    void createPersistsLibroAndResolvesRelations() {
        Libro toCreate = buildLibro("El Aleph", autorA.getId(), personaA.getId(), LocalDate.of(1949, 6, 1), "Ficción", 200);

        Libro saved = libroService.create(toCreate);

        assertNotNull(saved.getId());
        assertEquals(autorA.getId(), saved.getAutor().getId());
        assertEquals(personaA.getId(), saved.getPersona().getId());
        assertThat(libroRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void createWithFutureDateThrowsException() {
        Libro toCreate = buildLibro("Libro Futuro", autorA.getId(), personaA.getId(), LocalDate.now().plusDays(1), "Sci-Fi", 150);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> libroService.create(toCreate));
        assertThat(ex.getMessage()).contains("no puede ser futura");
    }

    @Test
    void updateMergesFieldsAndRelations() {
        Libro saved = libroService.create(buildLibro("El Aleph", autorA.getId(), personaA.getId(), LocalDate.of(1949, 6, 1), "Ficción", 200));

        Libro incoming = buildLibro("Rayuela", autorB.getId(), personaB.getId(), LocalDate.of(1963, 6, 28), "Novela", 400);
        Libro updated = libroService.update(saved.getId(), incoming);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Rayuela", updated.getTitulo());
        assertEquals(autorB.getId(), updated.getAutor().getId());
        assertEquals(personaB.getId(), updated.getPersona().getId());
    }

    @Test
    void deleteRemovesLibro() {
        Libro saved = libroService.create(buildLibro("El Aleph", autorA.getId(), personaA.getId(), LocalDate.of(1949, 6, 1), "Ficción", 200));

        libroService.delete(saved.getId());

        assertThat(libroRepository.findById(saved.getId())).isNotPresent();
    }

    @Test
    void deleteUnknownLibroThrowsException() {
        assertThrows(EntityNotFoundException.class, () -> libroService.delete(999L));
    }

    private Autor buildAutor(String nombre, String apellido) {
        Autor autor = new Autor();
        autor.setNombre(nombre);
        autor.setApellido(apellido);
        autor.setBiografia("Bio " + nombre);
        return autor;
    }

    private Localidad buildLocalidad(String denominacion) {
        Localidad localidad = new Localidad();
        localidad.setDenominacion(denominacion);
        return localidad;
    }

    private Persona buildPersona(String nombre, String apellido, int dni, String calle, int numero, Localidad localidad) {
        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setDni(dni);

        Domicilio domicilio = new Domicilio();
        domicilio.setCalle(calle);
        domicilio.setNumero(numero);
        domicilio.setLocalidad(localidad);

        persona.setDomicilio(domicilio);
        return persona;
    }

    private Libro buildLibro(String titulo, Long autorId, Long personaId, LocalDate fecha, String genero, int paginas) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setFecha(fecha);
        libro.setGenero(genero);
        libro.setPaginas(paginas);
        libro.setTipo(TipoLibro.FISICO);
        libro.setPesoGramos(350.0);
        libro.setTamanoMb(null);

        Autor autor = new Autor();
        autor.setId(autorId);
        libro.setAutor(autor);

        Persona persona = new Persona();
        persona.setId(personaId);
        libro.setPersona(persona);
        return libro;
    }
}
