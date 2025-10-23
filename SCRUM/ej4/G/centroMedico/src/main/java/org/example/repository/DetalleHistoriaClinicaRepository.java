package org.example.repository;

import org.example.entity.DetalleHistoriaClinica;
import org.example.entity.HistoriaClinica;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleHistoriaClinicaRepository extends BaseRepository<DetalleHistoriaClinica, String> {
    
    List<DetalleHistoriaClinica> findByHistoriaClinicaOrderByFechaHistoriaDesc(HistoriaClinica historiaClinica);
}
