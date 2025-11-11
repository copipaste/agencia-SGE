-- Script SQL para PostgreSQL
-- Base de datos del microservicio IA

CREATE TABLE IF NOT EXISTS alertas_cancelacion (
    id SERIAL PRIMARY KEY,
    venta_id VARCHAR(50) NOT NULL,
    cliente_id VARCHAR(50) NOT NULL,
    email_cliente VARCHAR(255) NOT NULL,
    nombre_cliente VARCHAR(255),
    nombre_paquete VARCHAR(255),
    destino VARCHAR(255),
    fecha_venta TIMESTAMP NOT NULL,
    monto_total DECIMAL(10, 2),
    probabilidad_cancelacion DECIMAL(5, 4),
    recomendacion VARCHAR(50),
    fecha_prediccion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recordatorio_enviado BOOLEAN DEFAULT FALSE,
    fecha_envio_recordatorio TIMESTAMP,
    estado_venta VARCHAR(50) DEFAULT 'Pendiente',

    CONSTRAINT uq_venta_id UNIQUE(venta_id)
);

CREATE INDEX idx_recordatorio_pendiente
ON alertas_cancelacion(recordatorio_enviado, fecha_venta, estado_venta)
WHERE recordatorio_enviado = FALSE AND estado_venta = 'Pendiente';

CREATE INDEX idx_fecha_venta
ON alertas_cancelacion(fecha_venta);

COMMENT ON TABLE alertas_cancelacion IS 'Registro de ventas con alto riesgo de cancelación para envío automático de recordatorios';
COMMENT ON COLUMN alertas_cancelacion.fecha_venta IS 'Fecha de inicio del viaje (MongoDB: Venta.fechaVenta)';
COMMENT ON COLUMN alertas_cancelacion.recordatorio_enviado IS 'TRUE si ya se envió el recordatorio al cliente';

