package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.ConfiguracionCorreoAutomaticoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.ConfiguracionCorreoAutomatico;
import com.uncuyo.greedy_cars.shared.template.entity.Empresa;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.ConfiguracionCorreoAutomaticoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ConfiguracionCorreoAutomaticoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.EmpresaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConfiguracionCorreoAutomaticoService
        extends BaseService<ConfiguracionCorreoAutomatico, String> {

    private final ConfiguracionCorreoAutomaticoRepository configuracionRepository;
    private final EmpresaRepository empresaRepository;
    private final ConfiguracionCorreoAutomaticoMapper mapper;

    public ConfiguracionCorreoAutomaticoService(
            ConfiguracionCorreoAutomaticoRepository configuracionRepository,
            EmpresaRepository empresaRepository,
            ConfiguracionCorreoAutomaticoMapper mapper) {
        super(configuracionRepository);
        this.configuracionRepository = configuracionRepository;
        this.empresaRepository = empresaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionCorreoAutomaticoDTO> listarActivosDTO() throws ErrorServiceException {
        List<ConfiguracionCorreoAutomatico> entidades = listarActivos();
        return entidades.stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ConfiguracionCorreoAutomaticoDTO> obtenerDTO(String id) throws ErrorServiceException {
        return obtener(id).map(mapper::toDTO);
    }

    public ConfiguracionCorreoAutomaticoDTO altaDTO(ConfiguracionCorreoAutomaticoDTO dto)
            throws ErrorServiceException {
        ConfiguracionCorreoAutomatico entidad = prepararEntidadDesdeDTO(dto);
        ConfiguracionCorreoAutomatico guardada = alta(entidad);
        return mapper.toDTO(guardada);
    }

    public Optional<ConfiguracionCorreoAutomaticoDTO> modificarDTO(
            String id, ConfiguracionCorreoAutomaticoDTO dto) throws ErrorServiceException {
        ConfiguracionCorreoAutomatico cambios = prepararEntidadDesdeDTO(dto);
        cambios.setId(id);
        return modificar(id, cambios).map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ConfiguracionCorreoAutomaticoDTO> listarPorEmpresa(String empresaId)
            throws ErrorServiceException {
        if (empresaId == null || empresaId.isBlank()) {
            throw new ErrorServiceException("Debe indicar la empresa");
        }
        return configuracionRepository.findAllByEmpresaIdAndEliminadoIsFalse(empresaId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ConfiguracionCorreoAutomatico> buscarActivaPorEmpresa(String empresaId) {
        if (empresaId == null || empresaId.isBlank()) {
            return Optional.empty();
        }
        return configuracionRepository.findFirstByEmpresaIdAndEliminadoIsFalse(empresaId);
    }

    private ConfiguracionCorreoAutomatico prepararEntidadDesdeDTO(ConfiguracionCorreoAutomaticoDTO dto)
            throws ErrorServiceException {
        if (dto == null) {
            throw new ErrorServiceException("Los datos de configuración son obligatorios");
        }
        ConfiguracionCorreoAutomatico entidad = mapper.toEntity(dto);
        entidad.setCorreo(dto.getCorreo());
        entidad.setClave(dto.getClave());
        entidad.setPuerto(dto.getPuerto());
        entidad.setSmtp(dto.getSmtp());
        entidad.setTls(dto.getTls());
        entidad.setEmpresa(obtenerEmpresaActiva(dto.getEmpresaId()));
        entidad.setEliminado(Boolean.FALSE);
        return entidad;
    }

    private Empresa obtenerEmpresaActiva(String empresaId) throws ErrorServiceException {
        if (empresaId == null || empresaId.isBlank()) {
            throw new ErrorServiceException("Debe indicar la empresa");
        }
        return empresaRepository.findByIdAndEliminadoIsFalse(empresaId)
                .orElseThrow(() -> new ErrorServiceException("Empresa no encontrada o eliminada"));
    }

    @Override
    protected void actualizarEntidad(ConfiguracionCorreoAutomatico existente,
                                     ConfiguracionCorreoAutomatico nueva) {
        if (nueva.getCorreo() != null) {
            existente.setCorreo(nueva.getCorreo());
        }
        if (nueva.getClave() != null) {
            existente.setClave(nueva.getClave());
        }
        if (nueva.getPuerto() != null) {
            existente.setPuerto(nueva.getPuerto());
        }
        if (nueva.getSmtp() != null) {
            existente.setSmtp(nueva.getSmtp());
        }
        if (nueva.getTls() != null) {
            existente.setTls(nueva.getTls());
        }
        if (nueva.getEmpresa() != null) {
            existente.setEmpresa(nueva.getEmpresa());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, ConfiguracionCorreoAutomatico entidad)
            throws ErrorServiceException {
        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }
        if (entidad == null) {
            throw new ErrorServiceException("Debe indicar la configuración de correo");
        }
        if (entidad.getCorreo() == null || entidad.getCorreo().isBlank()) {
            throw new ErrorServiceException("El correo remitente es obligatorio");
        }
        if (entidad.getClave() == null || entidad.getClave().isBlank()) {
            throw new ErrorServiceException("La clave es obligatoria");
        }
        if (entidad.getPuerto() == null || entidad.getPuerto().isBlank()) {
            throw new ErrorServiceException("El puerto SMTP es obligatorio");
        }
        if (entidad.getSmtp() == null || entidad.getSmtp().isBlank()) {
            throw new ErrorServiceException("El host SMTP es obligatorio");
        }
        if (entidad.getTls() == null) {
            entidad.setTls(Boolean.TRUE);
        }
        if (entidad.getEmpresa() == null || entidad.getEmpresa().getId() == null) {
            throw new ErrorServiceException("Debe asociar la configuración a una empresa");
        }
    }
}
