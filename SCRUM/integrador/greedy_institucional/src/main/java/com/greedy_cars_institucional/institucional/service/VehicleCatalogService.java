package com.greedy_cars_institucional.institucional.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.greedy_cars_institucional.institucional.config.GreedyApiProperties;
import com.greedy_cars_institucional.institucional.model.CatalogStats;
import com.greedy_cars_institucional.institucional.model.VehicleCardView;
import com.greedy_cars_institucional.institucional.model.VehicleCatalogData;
import com.greedy_cars_institucional.institucional.shared.template.dao.VehiculoDao;
import com.greedy_cars_institucional.institucional.shared.template.dto.CaracteristicaVehiculoDTO;
import com.greedy_cars_institucional.institucional.shared.template.dto.CostoVehiculoDTO;
import com.greedy_cars_institucional.institucional.shared.template.dto.VehiculoDTO;
import com.greedy_cars_institucional.institucional.shared.template.exception.ErrorServiceException;
import com.greedy_cars_institucional.institucional.shared.template.service.CaracteristicaVehiculoService;
import com.greedy_cars_institucional.institucional.shared.template.service.CostoVehiculoService;

@Service
public class VehicleCatalogService {

    public static final String FALLBACK_IMAGE_DATA_URI = "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'%20width='800'%20height='450'%3E%3Crect%20width='800'%20height='450'%20fill='%23f5f5f5'/%3E%3Ctext%20x='50%25'%20y='50%25'%20dominant-baseline='middle'%20text-anchor='middle'%20fill='%23999'%20font-size='32'%20font-family='Arial'%3ESin%20imagen%3C/text%3E%3C/svg%3E";

    private static final Logger log = LoggerFactory.getLogger(VehicleCatalogService.class);

    private final CaracteristicaVehiculoService caracteristicaVehiculoService;
    private final CostoVehiculoService costoVehiculoService;
    private final GreedyApiProperties apiProperties;
    private final VehiculoDao vehiculoDao;

    public VehicleCatalogService(
        CaracteristicaVehiculoService caracteristicaVehiculoService,
        CostoVehiculoService costoVehiculoService,
        GreedyApiProperties apiProperties,
        VehiculoDao vehiculoDao
    ) {
        this.caracteristicaVehiculoService = caracteristicaVehiculoService;
        this.costoVehiculoService = costoVehiculoService;
        this.apiProperties = apiProperties;
        this.vehiculoDao = vehiculoDao;
    }

    public VehicleCatalogData fetchCatalog() throws ErrorServiceException {
        List<VehicleCardView> vehicles = caracteristicaVehiculoService.listarActivos().stream()
            .map(this::mapToView)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .sorted(Comparator.comparing(VehicleCardView::getDisplayName))
            .collect(Collectors.toList());

        CatalogStats stats = buildStats(vehicles);
        return new VehicleCatalogData(vehicles, stats);
    }

    private Optional<VehicleCardView> mapToView(CaracteristicaVehiculoDTO feature) {
        if (feature == null || feature.getId() == null) {
            return Optional.empty();
        }

        Double currentCost = null;
        try {
            currentCost = costoVehiculoService.buscarCostoVigente(feature.getId())
                .map(CostoVehiculoDTO::getCosto)
                .orElse(null);
        } catch (ErrorServiceException e) {
            log.warn("No se pudo obtener el costo vigente para {}", feature.getId(), e);
        }

        // Count actual vehicles instead of using stored characteristic counts
        long totalUnits = 0;
        long availableUnits = 0;
        long rentedUnits = 0;

        try {
            totalUnits = vehiculoDao.countByCaracteristicaId(feature.getId());
            rentedUnits = vehiculoDao.countByCaracteristicaIdAndEstado(feature.getId(), "ALQUILADO");
            availableUnits = totalUnits - rentedUnits;
        } catch (Exception e) {
            log.warn("Error counting vehicles for characteristic {}, using stored values", feature.getId(), e);
            // Fallback to stored values if counting fails
            totalUnits = feature.getCantidadTotalVehiculo();
            availableUnits = Math.max(feature.getCantidadTotalVehiculo() - feature.getCantidadVehiculoAlquilado(), 0);
            rentedUnits = feature.getCantidadVehiculoAlquilado();
        }

        String imageUrl = CollectionUtils.isEmpty(feature.getImagenIds()) ? null
            : apiProperties.buildImageContentUrl(feature.getImagenIds().get(0));

        VehicleCardView view = new VehicleCardView(
            feature.getId(),
            feature.getMarca(),
            feature.getModelo(),
            feature.getAnio(),
            feature.getCantidadAsiento(),
            feature.getCantidadPuerta(),
            (int) totalUnits,
            (int) availableUnits,
            currentCost,
            imageUrl,
            resolveCategory(feature));
        return Optional.of(view);
    }

    private CatalogStats buildStats(List<VehicleCardView> vehicles) {
        int totalVehicles = vehicles.size();
        int totalUnits = vehicles.stream().mapToInt(VehicleCardView::getTotalUnits).sum();
        int availableUnits = vehicles.stream().mapToInt(VehicleCardView::getAvailableUnits).sum();
        log.debug("Catálogo cargado: {} vehículos, {} unidades disponibles", totalVehicles, availableUnits);
        return new CatalogStats(totalVehicles, totalUnits, availableUnits);
    }

    private String resolveCategory(CaracteristicaVehiculoDTO feature) {
        int seats = feature.getCantidadAsiento();
        if (seats >= 7) {
            return "love"; // suv
        }
        if (seats <= 4) {
            return "life"; // coupe
        }
        return "travel"; // sedan
    }
}
