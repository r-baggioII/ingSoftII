package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Direccion;
import com.example.greedy_empresa.entidades.Localidad;
import com.example.greedy_empresa.entidades.Persona;
import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.repositorios.DireccionRepository;
import com.example.greedy_empresa.repositorios.LocalidadRepository;
import com.example.greedy_empresa.repositorios.PersonaRepository;
import com.example.greedy_empresa.repositorios.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class MigracionService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    @Transactional
    public int procesarArchivoMigracion(MultipartFile archivo) throws IOException {
        int proveedoresCreados = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            
            String linea;
            int numeroLinea = 0;
            
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim();
                
                if (linea.isEmpty()) {
                    continue; // Saltar líneas vacías
                }
                
                try {
                    if (procesarLineaProveedor(linea)) {
                        proveedoresCreados++;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error en línea " + numeroLinea + ": " + e.getMessage(), e);
                }
            }
        }

        return proveedoresCreados;
    }

    private boolean procesarLineaProveedor(String linea) {
        // Formato: NOMBRE;APELLIDO;TELEFONO;CORREO_ELECTRONICO;CUIT;CALLE;NUMERO;LOCALIDAD;DEPARTAMENTO;PROVINCIA;PAIS;CODIGO_POSTAL
        String[] campos = linea.split(";");
        
        if (campos.length < 12) {
            throw new RuntimeException("La línea debe tener exactamente 12 campos separados por ';'. " +
                    "Formato: NOMBRE;APELLIDO;TELEFONO;CORREO_ELECTRONICO;CUIT;CALLE;NUMERO;LOCALIDAD;DEPARTAMENTO;PROVINCIA;PAIS;CODIGO_POSTAL");
        }

        String nombre = campos[0].trim();
        String apellido = campos[1].trim();
        String telefono = campos[2].trim();
        String correoElectronico = campos[3].trim();
        String cuit = campos[4].trim();
        String calle = campos[5].trim();
        String numero = campos[6].trim();
        String nombreLocalidad = campos[7].trim();
        String nombreDepartamento = campos[8].trim();
        String nombreProvincia = campos[9].trim();
        String nombrePais = campos[10].trim();
        String codigoPostal = campos[11].trim();

        // Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || correoElectronico.isEmpty() || cuit.isEmpty()) {
            throw new RuntimeException("Los campos nombre, apellido, correo electrónico y CUIT son obligatorios");
        }

        // Verificar si el proveedor ya existe por CUIT
        Optional<Proveedor> proveedorExistente = proveedorRepository.findByCuitIgnoreCaseAndEliminadoFalse(cuit);
        if (proveedorExistente.isPresent()) {
            // El proveedor ya existe, no se procesa
            return false;
        }

        // Buscar la localidad por código postal primero, si no existe intentar por nombre
        Optional<Localidad> localidadOpt = buscarLocalidad(nombreLocalidad, codigoPostal);
        if (localidadOpt.isEmpty()) {
            throw new RuntimeException("No se encontró la localidad: " + nombreLocalidad + 
                    " con código postal: " + codigoPostal + 
                    " en " + nombreDepartamento + ", " + nombreProvincia + ", " + nombrePais);
        }

        Localidad localidad = localidadOpt.get();

        // Crear la persona
        Persona persona = new Persona();
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setTelefono(telefono.isEmpty() ? null : telefono);
        persona.setCorreoElectronico(correoElectronico);
        persona = personaRepository.save(persona);

        // Crear el proveedor
        Proveedor proveedor = new Proveedor();
        proveedor.setCuit(cuit);
        proveedor = proveedorRepository.save(proveedor);

        // Crear la dirección
        Direccion direccion = new Direccion();
        direccion.setCalle(calle);
        direccion.setNumero(numero);
        direccion.setLocalidad(localidad);
        direccion.setPersona(persona);
        direccion.setProveedor(proveedor);
        direccionRepository.save(direccion);

        return true;
    }

    private Optional<Localidad> buscarLocalidad(String nombreLocalidad, String codigoPostal) {
        // Primero buscar por código postal
        if (!codigoPostal.isEmpty()) {
            var localidadesPorCodigoPostal = localidadRepository.findByCodigoPostalContainingIgnoreCaseAndEliminadoFalse(codigoPostal);
            if (!localidadesPorCodigoPostal.isEmpty()) {
                // Si hay múltiples localidades con el mismo código postal, elegir la primera que coincida con el nombre
                for (Localidad localidad : localidadesPorCodigoPostal) {
                    if (localidad.getNombre().equalsIgnoreCase(nombreLocalidad)) {
                        return Optional.of(localidad);
                    }
                }
                // Si no hay coincidencia exacta de nombre, tomar la primera
                return Optional.of(localidadesPorCodigoPostal.get(0));
            }
        }

        // Si no se encuentra por código postal, buscar por nombre en toda la base de datos
        var todasLasLocalidades = localidadRepository.findAll();
        for (Localidad localidad : todasLasLocalidades) {
            if (localidad.getNombre().equalsIgnoreCase(nombreLocalidad) && !localidad.isEliminado()) {
                return Optional.of(localidad);
            }
        }

        return Optional.empty();
    }
}