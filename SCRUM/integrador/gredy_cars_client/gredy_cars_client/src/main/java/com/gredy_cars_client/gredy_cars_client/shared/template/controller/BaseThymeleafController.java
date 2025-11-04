package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.BaseDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseController;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import com.gredy_cars_client.gredy_cars_client.shared.template.service.BaseClientService;

/**
 * Template controller for Thymeleaf-based flows. It centralises repetitive
 * logic such as populating the model, redirecting with flash attributes and
 * handling service exceptions. Concrete controllers only need to specify the
 * view names, attribute keys and the entity name.
 *
 * @param <T>  dto type
 * @param <ID> identifier type
 */
public abstract class BaseThymeleafController<T extends BaseDTO<ID>, ID> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected final BaseClientService<T, ID> service;

    protected BaseThymeleafController(BaseClientService<T, ID> service) {
        this.service = service;
    }

    protected abstract String getListView();

    protected abstract String getFormView();

    protected abstract String getRedirectToList();

    protected abstract String getListModelAttribute();

    protected abstract String getFormModelAttribute();

    protected abstract String getEntityLabel();

    protected abstract T buildNewInstance();

    protected void preUseCase(BaseUseCaseController useCase, Model model) throws ErrorServiceException {}

    protected void postUseCase(BaseUseCaseController useCase, Model model) throws ErrorServiceException {}

    protected void postSuccess(BaseUseCaseController useCase, RedirectAttributes redirectAttributes, T payload) {}

    protected void registerSuccess(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("success", message);
    }

    protected void registerError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("error", message);
    }

    protected void registerError(Model model, String message) {
        model.addAttribute("error", message);
    }

    protected void populateCollections(Model model) throws ErrorServiceException {}

    public String renderList(Model model) {
        try {
            preUseCase(BaseUseCaseController.LISTAR, model);
            List<T> listado = service.listarActivos();
            model.addAttribute(getListModelAttribute(), listado);
            postUseCase(BaseUseCaseController.LISTAR, model);
        } catch (ErrorServiceException e) {
            log.error("Error al listar {}", getEntityLabel(), e);
            registerError(model, e.getMessage());
        }
        return getListView();
    }

    public String renderCreateForm(Model model) {
        try {
            preUseCase(BaseUseCaseController.ALTA, model);
            populateCollections(model);
            if (!model.containsAttribute(getFormModelAttribute())) {
                model.addAttribute(getFormModelAttribute(), buildNewInstance());
            }
            postUseCase(BaseUseCaseController.ALTA, model);
        } catch (ErrorServiceException e) {
            log.error("Error al preparar formulario de {}", getEntityLabel(), e);
            registerError(model, e.getMessage());
        }
        return getFormView();
    }

    public String handleCreate(T dto, Model model, RedirectAttributes redirectAttributes) {
        try {
            T creado = service.alta(dto);
            registerSuccess(redirectAttributes, getEntityLabel() + " creado correctamente");
            postSuccess(BaseUseCaseController.ALTA, redirectAttributes, creado);
            return getRedirectToList();
        } catch (ErrorServiceException e) {
            log.warn("Validación al crear {}: {}", getEntityLabel(), e.getMessage());
            registerError(model, e.getMessage());
            model.addAttribute(getFormModelAttribute(), dto);
            try {
                populateCollections(model);
            } catch (ErrorServiceException populateEx) {
                log.error("Error al recargar datos del formulario de {}", getEntityLabel(), populateEx);
                registerError(model, populateEx.getMessage());
            }
            return getFormView();
        }
    }

    public String renderEditForm(ID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            preUseCase(BaseUseCaseController.MODIFICACION, model);
            Optional<T> dto = service.obtener(id);
            if (dto.isEmpty()) {
                registerError(redirectAttributes, getEntityLabel() + " no encontrado");
                return getRedirectToList();
            }
            model.addAttribute(getFormModelAttribute(), dto.get());
            populateCollections(model);
            postUseCase(BaseUseCaseController.MODIFICACION, model);
            return getFormView();
        } catch (ErrorServiceException e) {
            log.error("Error al cargar {} con id {}", getEntityLabel(), id, e);
            registerError(redirectAttributes, e.getMessage());
            return getRedirectToList();
        }
    }

    public String handleUpdate(ID id, T dto, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<T> actualizado = service.modificar(id, dto);
            if (actualizado.isEmpty()) {
                registerError(redirectAttributes, getEntityLabel() + " no encontrado");
                return getRedirectToList();
            }
            registerSuccess(redirectAttributes, getEntityLabel() + " actualizado correctamente");
            postSuccess(BaseUseCaseController.MODIFICACION, redirectAttributes, actualizado.get());
            return getRedirectToList();
        } catch (ErrorServiceException e) {
            log.warn("Validación al actualizar {} con id {}: {}", getEntityLabel(), id, e.getMessage());
            registerError(model, e.getMessage());
            dto.setId(id);
            model.addAttribute(getFormModelAttribute(), dto);
            try {
                populateCollections(model);
            } catch (ErrorServiceException populateEx) {
                log.error("Error al recargar datos del formulario de {}", getEntityLabel(), populateEx);
                registerError(model, populateEx.getMessage());
            }
            return getFormView();
        }
    }

    public String handleDelete(ID id, RedirectAttributes redirectAttributes) {
        try {
            service.baja(id);
            registerSuccess(redirectAttributes, getEntityLabel() + " eliminado correctamente");
            postSuccess(BaseUseCaseController.BAJA, redirectAttributes, null);
        } catch (ErrorServiceException e) {
            log.error("Error al eliminar {} con id {}", getEntityLabel(), id, e);
            registerError(redirectAttributes, e.getMessage());
        }
        return getRedirectToList();
    }
}

