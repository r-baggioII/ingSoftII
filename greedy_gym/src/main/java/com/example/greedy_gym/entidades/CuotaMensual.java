package com.example.greedy_gym.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "cuota_mensual", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cuota_socio_mes_anio_eliminado",
                columnNames = {"id_socio", "mes", "anio", "eliminado"})
})
public class CuotaMensual {

    @Id
    private String id;

    @Column(name = "id_socio", nullable = false)
    private String idSocio;

    @Enumerated(EnumType.STRING)
    @Column(name = "mes", nullable = false)
    private Mes mes;

    @Column(name = "anio", nullable = false)
    private Long anio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCuota estado = EstadoCuota.PENDIENTE;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "valor_cuota_id", nullable = false, updatable = false)
    private ValorCuota valorCuota;

    // Campos calculados para UI (no persistidos)
    @Transient
    private String socioNumeroDocumento;

    @Transient
    private Long socioNumeroSocio;

    @Transient
    private String socioNombreCompleto;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.estado == null) {
            this.estado = EstadoCuota.PENDIENTE;
        }
    }
}
