package org.contactoEmpresa.controller;

import java.util.List;

import org.contactoEmpresa.entity.BaseEntity;
import org.contactoEmpresa.service.BaseService;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.contactoEmpresa.enums.BaseUseCaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class BaseController<T extends BaseEntity<ID>, ID> {

    protected final BaseService<T, ID> service;
    private String nameClass = "";
    protected String viewList = "";
    protected String redirectList = "";
    protected String viewEdit = "";
    protected T entity;
    protected Class<T> entityClass;
    protected String nameEntityLower;
    protected Model model;
    protected String titleList;
    protected String titleEdit;
    protected String errorMenssage;

    protected BaseController(BaseService<T, ID> service) {
        this.service = service;
    }

    protected void initController(T entity, String titleList, String titleEdit) {
        this.entity = entity;
        this.entityClass = (Class<T>) entity.getClass();
        this.nameClass = getNameEntity(this.entity);
        this.nameEntityLower = nameClass.toLowerCase();

        this.viewList = nameEntityLower + "-list";
        this.redirectList = "redirect:/" + nameEntityLower + "/list";
        this.viewEdit = nameEntityLower + "-form";
        this.titleList = titleList;
        this.titleEdit = titleEdit;
    }

    private String getNameEntity(T object) {
        return ((((T) object).getClass()).getSimpleName());
    }

    @GetMapping("/list")
    public String list(Model model) {
        try {
            this.model = model;
            List<T> listEntity = service.listarActivos();
            this.model.addAttribute("items", listEntity);
            this.model.addAttribute("titleList", titleList);
            this.model.addAttribute("nameEntityLower", nameEntityLower);

        } catch (ErrorServiceException e) {
            showErrorMenssage(e.getMessage(), BaseUseCaseController.LISTAR);
        } catch (Exception e) {
            this.model.addAttribute("msgError", "Error de Sistema");
            showErrorMenssage(e.getMessage(), BaseUseCaseController.LISTAR);
        }

        return viewList;
    }

    @GetMapping("/alta")
    public String crear(Model model) {
        try {
            this.model = model;
            // Crear nueva instancia de la entidad usando el tipo guardado
            try {
                this.entity = entityClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new ErrorServiceException("Error al crear nueva instancia de " + nameClass);
            }

            this.model.addAttribute("item", this.entity);
            this.model.addAttribute("isDisabled", false);
            this.model.addAttribute("titleEdit", titleEdit);
            this.model.addAttribute("nameEntityLower", nameEntityLower);

            preUseCase(BaseUseCaseController.ALTA);

            return viewEdit;

        } catch (ErrorServiceException e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.ALTA);
            return viewEdit;
        } catch (Exception e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.ALTA);
            return viewEdit;
        }
    }

    @GetMapping("/consultar/{id}")
    public String consultar(@PathVariable ID id, Model model) {
        try {
            this.model = model;
            this.entity = service.obtener(id).orElseThrow(() -> new IllegalArgumentException("No encontrado: " + id));
            this.model.addAttribute("item", entity);
            this.model.addAttribute("isDisabled", true);
            this.model.addAttribute("titleEdit", titleEdit);
            this.model.addAttribute("nameEntityLower", nameEntityLower);

            preUseCase(BaseUseCaseController.CONSULTAR);

            return viewEdit;

        } catch (ErrorServiceException e) {
            this.model.addAttribute("isDisabled", true);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.CONSULTAR);
            return viewEdit;
        } catch (Exception e) {
            this.model.addAttribute("isDisabled", true);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.CONSULTAR);
            return viewEdit;
        }
    }

    @GetMapping("/modificar/{id}")
    public String editar(@PathVariable ID id, Model model) {
        try {
            this.model = model;
            this.entity = service.obtener(id).orElseThrow(() -> new IllegalArgumentException("No encontrado: " + id));
            this.model.addAttribute("item", entity);
            this.model.addAttribute("isDisabled", false);
            this.model.addAttribute("titleEdit", titleEdit);
            this.model.addAttribute("nameEntityLower", nameEntityLower);
            preUseCase(BaseUseCaseController.MODIFICACION);

            return viewEdit;

        } catch (ErrorServiceException e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.MODIFICACION);
            return viewEdit;
        } catch (Exception e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.MODIFICACION);
            return viewEdit;
        }
    }

    @GetMapping("/baja/{id}")
    public String eliminar(@PathVariable ID id, RedirectAttributes attributes, Model model) {
        try {
            this.model = model;

            preUseCase(BaseUseCaseController.BAJA);
            service.baja(id);

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return redirectList;

        } catch (ErrorServiceException e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.BAJA);
            return viewEdit;
        } catch (Exception e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.BAJA);
            return viewEdit;
        }
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("item") T entity, RedirectAttributes attributes, Model model) {
        try {
            this.model = model;
            this.entity = entity;
            preUseCase(BaseUseCaseController.ACTUALIZAR);

            if (this.entity.getId() == null || (this.entity.getId() instanceof String && ((String)this.entity.getId()).isEmpty()))
                service.alta(this.entity);
            else
                service.modificar(this.entity.getId(), this.entity);

            postUseCase(BaseUseCaseController.ACTUALIZAR);

            attributes.addFlashAttribute("msgExito", "La acción fue realizada correctamente.");
            return redirectList;

        } catch (ErrorServiceException e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.ACTUALIZAR);
            return viewEdit;
        } catch (Exception e) {
            this.model.addAttribute("isDisabled", false);
            showErrorMenssage(e.getMessage(), BaseUseCaseController.ACTUALIZAR);
            return viewEdit;
        }
    }

    @GetMapping("/cancelar")
    public String cancelar() {
        return redirectList;
    }

    protected String showErrorMenssage(String mensaje, BaseUseCaseController useCase) {
        try {
            this.model.addAttribute("titleEdit", titleEdit);
            this.model.addAttribute("nameEntityLower", nameEntityLower);
            this.model.addAttribute("msgError", mensaje);

            // Asegurar que isDisabled esté presente en el modelo
            if (!this.model.containsAttribute("isDisabled")) {
                this.model.addAttribute("isDisabled", false);
            }

            if (this.entity.getId() != null) {
                this.model.addAttribute("item", service.obtenerEntidad(this.entity.getId()));
            } else {
                this.model.addAttribute("item", loadingEntityForFormByError(useCase, model, this.entity));
            }

        } catch (Exception e) {}

        return viewEdit;
    }

    // Métodos para ser redefinidos en los controladores que heredan, con el objetivo
    // que sea necesario realizar acciones previas o posteriores en las Altas, Bajas y
    // Modificaciones.
    // Se deberá redefinir el comportamiento en la clase que hereda.
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {}
    protected void postUseCase(BaseUseCaseController useCase) throws ErrorServiceException {}
    protected T loadingEntityForFormByError(BaseUseCaseController useCase, Model model, T entity) throws ErrorServiceException {
        return entity;
    }
}