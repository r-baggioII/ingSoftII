package com.ejemplo.biblioteca.config;

import com.ejemplo.biblioteca.domain.Autor;
import com.ejemplo.biblioteca.domain.Domicilio;
import com.ejemplo.biblioteca.domain.Libro;
import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.repository.AutorRepository;
import com.ejemplo.biblioteca.repository.LibroRepository;
import com.ejemplo.biblioteca.repository.LocalidadRepository;
import com.ejemplo.biblioteca.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.time.LocalDate;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevDataLoader {

    private final LocalidadRepository localidadRepository;
    private final PersonaRepository personaRepository;
    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;

    @Bean
    public CommandLineRunner loadSampleData() {
        return args -> {
            if (personaRepository.count() > 0 || autorRepository.count() > 0 || localidadRepository.count() > 0 || libroRepository.count() > 0) {
                return;
            }

            Localidad loc1 = new Localidad();
            loc1.setDenominacion("Buenos Aires");
            Localidad loc2 = new Localidad();
            loc2.setDenominacion("Córdoba");
            Localidad loc3 = new Localidad();
            loc3.setDenominacion("Mendoza");
            localidadRepository.save(loc1);
            localidadRepository.save(loc2);
            localidadRepository.save(loc3);

            Persona persona1 = buildPersona("Ana", "Pérez", 12345678, "San Martín", 550, loc1);
            Persona persona2 = buildPersona("Luis", "Gómez", 87654321, "Belgrano", 1200, loc2);
            personaRepository.save(persona1);
            personaRepository.save(persona2);

            Autor autor1 = buildAutor("Jorge", "Luis Borges", "Autor emblemático de la literatura argentina.");
            Autor autor2 = buildAutor("Julio", "Cortázar", "Escritor y traductor argentino, figura clave del boom latinoamericano.");
            Autor autor3 = buildAutor("Adriana", "Sández", "Autora contemporánea enfocada en tecnologías emergentes.");
            autorRepository.save(autor1);
            autorRepository.save(autor2);
            autorRepository.save(autor3);

            Libro libroConPdf = libroRepository.save(buildLibro("El Aleph", LocalDate.of(1949, 6, 1), "Ficción", 200, autor1, persona1));
            libroConPdf.setPdfFileName("libro_el_aleph_" + libroConPdf.getId() + ".pdf");
            libroConPdf.setPdfSizeBytes(1024L);
            libroConPdf.setPdfUploadedAt(Instant.now());
            libroRepository.save(libroConPdf);
            libroRepository.save(buildLibro("Ficciones", LocalDate.of(1944, 5, 1), "Ficción", 250, autor1, persona1));
            libroRepository.save(buildLibro("Rayuela", LocalDate.of(1963, 6, 28), "Novela", 400, autor2, persona2));
            libroRepository.save(buildLibro("Historias de Cronopios y de Famas", LocalDate.of(1962, 1, 1), "Cuento", 210, autor2, persona2));
            libroRepository.save(buildLibro("IA Moderna", LocalDate.of(2023, 10, 1), "Tecnología", 420, autor3, persona1));
        };
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

    private Autor buildAutor(String nombre, String apellido, String biografia) {
        Autor autor = new Autor();
        autor.setNombre(nombre);
        autor.setApellido(apellido);
        autor.setBiografia(biografia);
        return autor;
    }

    private Libro buildLibro(String titulo, LocalDate fecha, String genero, int paginas, Autor autor, Persona persona) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setFecha(fecha);
        libro.setGenero(genero);
        libro.setPaginas(paginas);
        libro.setAutor(autor);
        libro.setPersona(persona);
        return libro;
    }
}
