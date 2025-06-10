package tescha.prestamos.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PrestamoRecurrenteDTO {
    private int id;
    private int solicitanteId;
    private int equipoId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int frecuencia; // en días
    private String comentarios;
    private boolean activo;

    public enum Frecuencia {
        DIARIO(1),
        SEMANAL(7),
        QUINCENAL(15),
        MENSUAL(30),
        BIMESTRAL(60),
        TRIMESTRAL(90),
        SEMESTRAL(180),
        ANUAL(365);

        private final int dias;

        Frecuencia(int dias) {
            this.dias = dias;
        }

        public int getDias() {
            return dias;
        }
    }

    // Constructores, getters y setters
    public PrestamoRecurrenteDTO() {}

    public PrestamoRecurrenteDTO(int solicitanteId, int equipoId, LocalDate fechaInicio,
                                 LocalDate fechaFin, int frecuencia, String comentarios, boolean activo) {
        this.solicitanteId = solicitanteId;
        this.equipoId = equipoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.frecuencia = frecuencia;
        this.comentarios = comentarios;
        this.activo = activo;
    }

    // Getters y setters...

    public LocalDate calcularProximaFecha() {
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(fechaInicio)) return fechaInicio;

        long diasTranscurridos = ChronoUnit.DAYS.between(fechaInicio, hoy);
        long periodosCompletos = diasTranscurridos / frecuencia;
        return fechaInicio.plusDays((periodosCompletos + 1) * frecuencia);
    }
}