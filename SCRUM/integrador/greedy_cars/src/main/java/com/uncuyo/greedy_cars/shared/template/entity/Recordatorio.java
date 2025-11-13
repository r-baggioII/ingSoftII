package com.uncuyo.greedy_cars.shared.template.entity;

import com.uncuyo.greedy_cars.shared.template.enums.EstadoRecordatorio;
import com.uncuyo.greedy_cars.shared.template.enums.TipoRecordatorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "alquiler", callSuper = true)
@Entity
@Table(name = "recordatorio")
public class Recordatorio extends BaseEntity<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alquiler_id", nullable = false)
    private Alquiler alquiler;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recordatorio", nullable = false, length = 30)
    private TipoRecordatorio tipoRecordatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoRecordatorio estado = EstadoRecordatorio.ENVIADO;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "detalle_error", length = 400)
    private String detalleError;

    public Recordatorio(Alquiler alquiler,
                        TipoRecordatorio tipoRecordatorio,
                        EstadoRecordatorio estado,
                        String detalleError) {
        this.alquiler = alquiler;
        this.tipoRecordatorio = tipoRecordatorio;
        this.estado = estado != null ? estado : EstadoRecordatorio.ENVIADO;
        this.detalleError = detalleError;
    }

    @PrePersist
    public void prePersist() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
