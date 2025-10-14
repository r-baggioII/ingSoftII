package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Departamento;
import com.example.greedy_empresa.entidades.Empresa;
import com.example.greedy_empresa.entidades.Localidad;
import com.example.greedy_empresa.entidades.Pais;
import com.example.greedy_empresa.entidades.Provincia;
import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.entidades.Usuario;
import com.example.greedy_empresa.entidades.enums.UsuarioRol;
import com.example.greedy_empresa.repositorios.DepartamentoRepository;
import com.example.greedy_empresa.repositorios.EmpresaRepository;
import com.example.greedy_empresa.repositorios.LocalidadRepository;
import com.example.greedy_empresa.repositorios.PaisRepository;
import com.example.greedy_empresa.repositorios.ProvinciaRepository;
import com.example.greedy_empresa.repositorios.ProveedorRepository;
import com.example.greedy_empresa.repositorios.UsuarioRepository;
import com.example.greedy_empresa.servicios.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner loadData(PaisRepository paisRepository,
            ProvinciaRepository provinciaRepository,
            DepartamentoRepository departamentoRepository,
            LocalidadRepository localidadRepository,
            EmpresaRepository empresaRepository,
            ProveedorRepository proveedorRepository,
            UsuarioRepository usuarioRepository,
            PasswordService passwordService) {

        return args -> {
            crearAdmin(usuarioRepository, passwordService);

            Pais pais = paisRepository.findByNombreIgnoreCase("Argentina")
                    .orElseGet(() -> {
                        Pais nuevo = new Pais();
                        nuevo.setNombre("Argentina");
                        return paisRepository.save(nuevo);
                    });

            Provincia provincia = provinciaRepository
                    .findByNombreIgnoreCaseAndPais_Id("Buenos Aires", pais.getId())
                    .orElseGet(() -> {
                        Provincia nuevo = new Provincia();
                        nuevo.setNombre("Buenos Aires");
                        nuevo.setPais(pais);
                        return provinciaRepository.save(nuevo);
                    });

            Departamento departamento = departamentoRepository
                    .findByNombreIgnoreCaseAndProvincia_Id("La Plata", provincia.getId())
                    .orElseGet(() -> {
                        Departamento nuevo = new Departamento();
                        nuevo.setNombre("La Plata");
                        nuevo.setProvincia(provincia);
                        return departamentoRepository.save(nuevo);
                    });

            localidadRepository.findByNombreIgnoreCaseAndDepartamento_Id("Centro", departamento.getId())
                    .orElseGet(() -> {
                        Localidad nuevo = new Localidad();
                        nuevo.setNombre("Centro");
                        nuevo.setCodigoPostal("1900");
                        nuevo.setDepartamento(departamento);
                        return localidadRepository.save(nuevo);
                    });

            empresaRepository.findByRazonSocialIgnoreCase("Greedy Corp")
                    .orElseGet(() -> {
                        Empresa empresa = new Empresa();
                        empresa.setRazonSocial("Greedy Corp");
                        return empresaRepository.save(empresa);
                    });

            proveedorRepository.findByCuitIgnoreCase("30-12345678-9")
                    .orElseGet(() -> {
                        Proveedor proveedor = new Proveedor();
                        proveedor.setCuit("30-12345678-9");
                        return proveedorRepository.save(proveedor);
                    });
        };
    }

    private void crearAdmin(UsuarioRepository usuarioRepository, PasswordService passwordService) {
        usuarioRepository.findByUsernameIgnoreCase("admin")
                .map(usuario -> {
                    if (usuario.isEliminado()) {
                        usuario.setEliminado(false);
                        usuario.setPasswordHash(passwordService.hash("Admin123!"));
                        usuario.setRol(UsuarioRol.ADMIN);
                        return usuarioRepository.save(usuario);
                    }
                    return usuario;
                })
                .orElseGet(() -> {
                    Usuario admin = new Usuario();
                    admin.setUsername("admin");
                    admin.setPasswordHash(passwordService.hash("Admin123!"));
                    admin.setRol(UsuarioRol.ADMIN);
                    return usuarioRepository.save(admin);
                });
    }
}
