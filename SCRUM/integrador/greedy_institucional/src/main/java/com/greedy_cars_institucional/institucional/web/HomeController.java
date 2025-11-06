package com.greedy_cars_institucional.institucional.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.greedy_cars_institucional.institucional.model.VehicleCatalogData;
import com.greedy_cars_institucional.institucional.service.VehicleCatalogService;
import com.greedy_cars_institucional.institucional.shared.template.exception.ErrorServiceException;

@Controller
public class HomeController {

    private final VehicleCatalogService catalogService;

    public HomeController(VehicleCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping({ "/", "/institucional", "/institucional/index" })
    public String index(Model model) {
        VehicleCatalogData catalog;
        try {
            catalog = catalogService.fetchCatalog();
        } catch (ErrorServiceException e) {
            catalog = VehicleCatalogData.empty();
            model.addAttribute("catalogError", "No se pudo sincronizar el catálogo de vehículos. Intenta nuevamente más tarde.");
        }
        model.addAttribute("vehicleCards", catalog.getVehicles());
        model.addAttribute("catalogStats", catalog.getStats());
        model.addAttribute("fallbackImage", VehicleCatalogService.FALLBACK_IMAGE_DATA_URI);
        return "institucional/index";
    }
}
