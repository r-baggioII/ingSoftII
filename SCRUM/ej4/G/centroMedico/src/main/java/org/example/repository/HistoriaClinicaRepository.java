package org.example.repository;

import org.example.entity.HistoriaClinica;
import org.example.entity.Paciente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaClinicaRepository extends BaseRepository<HistoriaClinica, String> {
    
    Optional<HistoriaClinica> findByPaciente(Paciente paciente);
    
    boolean existsByPaciente(Paciente paciente);
    
    @Query("SELECT DISTINCT h FROM HistoriaClinica h LEFT JOIN FETCH h.detalles LEFT JOIN FETCH h.paciente WHERE h.eliminado = false")
    List<HistoriaClinica> findAllWithDetalles();
    
    @Query("SELECT h FROM HistoriaClinica h LEFT JOIN FETCH h.detalles LEFT JOIN FETCH h.paciente WHERE h.id = :id")
    Optional<HistoriaClinica> findByIdWithDetalles(String id);
}
