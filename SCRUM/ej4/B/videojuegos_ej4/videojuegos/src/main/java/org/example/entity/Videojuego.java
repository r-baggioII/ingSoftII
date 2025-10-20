package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
@Table(name = "videojuegos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Videojuego extends BaseEntity {

    @NotEmpty(message = "{NotEmpty.Videojuego.titulo}")
    private String titulo;
    @Size(min=5,max=100,message= "La descripcion debe ser entre 5 y 100 caracteres")
    private String descripcion;

    private String imagen;

    @Min(value = 5,message="El precio debe tener un minimo de 5")
    @Max(value = 10000, message="El precio debe ser menor a 1000")
    private float precio;
    @Min(value = 1,message="El stock debe tener un minimo de 5")
    @Max(value = 10000, message="El stock debe ser menor a 1000")
    private short stock;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message="No puede ser nulo la fecha")
    @PastOrPresent(message="Debe ser igual o menor a la fecha de hoy")
    private Date fechaLanzamiento;

    @NotNull(message="Es requerido el estudio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_estudio", nullable = false)
    private Estudio estudio;

    @NotNull(message="Es requerida la categoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_categoria", nullable = false)
    private Categoria categoria;

    // Getters y Setters explícitos (Lombok no está funcionando correctamente)
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public short getStock() {
        return stock;
    }

    public void setStock(short stock) {
        this.stock = stock;
    }

    public Date getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(Date fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public Estudio getEstudio() {
        return estudio;
    }

    public void setEstudio(Estudio estudio) {
        this.estudio = estudio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}