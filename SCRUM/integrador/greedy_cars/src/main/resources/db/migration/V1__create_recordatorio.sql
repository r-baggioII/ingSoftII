CREATE TABLE IF NOT EXISTS recordatorio (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    alquiler_id VARCHAR(36) NOT NULL,
    tipo_recordatorio VARCHAR(30) NOT NULL,
    fecha_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL,
    detalle_error VARCHAR(400),
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_recordatorio_alquiler FOREIGN KEY (alquiler_id) REFERENCES alquiler(id)
);

CREATE INDEX IF NOT EXISTS idx_recordatorio_alquiler_tipo
    ON recordatorio(alquiler_id, tipo_recordatorio);
