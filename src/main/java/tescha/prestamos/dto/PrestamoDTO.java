package tescha.prestamos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para representar un préstamo en el sistema
 */
public class PrestamoDTO {
    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private LocalDate fechaDevolucion;
    private int solicitanteId;
    private String solicitanteNombre;
    private int idEquipo;
    private String nombreEquipo;
    private String marcaEquipo;
    private String modeloEquipo;
    private int cantidad;
    private String comentarios;
    private String condiciones;
    private String entrega;
    private String entregadoPor;
    private boolean devuelto;
    private LocalDate fechaDevuelto;
    private LocalTime horaDevuelto;
    private String devueltoPor;
    private String recibidoPor;
    private String estadoDevuelto;
    private String tipoEntrega;
    private String status; // ACTIVO, VENCIDO, DEVUELTO
    private int diasRestantes;

    // Constructor vacío
    public PrestamoDTO() {
        this.tipoEntrega = "manual";
        this.devuelto = false;
        this.cantidad = 1;
    }

    // Constructor completo
    public PrestamoDTO(int id, LocalDate fecha, LocalTime hora, LocalDate fechaDevolucion,
                       int solicitanteId, String solicitanteNombre, int idEquipo, String nombreEquipo,
                       String marcaEquipo, String modeloEquipo, int cantidad, String comentarios,
                       String condiciones, String entrega, String entregadoPor, boolean devuelto,
                       LocalDate fechaDevuelto, LocalTime horaDevuelto, String devueltoPor,
                       String recibidoPor, String estadoDevuelto, String tipoEntrega) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.fechaDevolucion = fechaDevolucion;
        this.solicitanteId = solicitanteId;
        this.solicitanteNombre = solicitanteNombre;
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.marcaEquipo = marcaEquipo;
        this.modeloEquipo = modeloEquipo;
        this.cantidad = cantidad;
        this.comentarios = comentarios;
        this.condiciones = condiciones;
        this.entrega = entrega;
        this.entregadoPor = entregadoPor;
        this.devuelto = devuelto;
        this.fechaDevuelto = fechaDevuelto;
        this.horaDevuelto = horaDevuelto;
        this.devueltoPor = devueltoPor;
        this.recibidoPor = recibidoPor;
        this.estadoDevuelto = estadoDevuelto;
        this.tipoEntrega = tipoEntrega;
        this.status = calculateStatus();
        this.diasRestantes = calculateDiasRestantes();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
        this.diasRestantes = calculateDiasRestantes();
        this.status = calculateStatus();
    }

    public int getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(int solicitanteId) { this.solicitanteId = solicitanteId; }

    public String getSolicitanteNombre() { return solicitanteNombre; }
    public void setSolicitanteNombre(String solicitanteNombre) { this.solicitanteNombre = solicitanteNombre; }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }

    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }

    public String getMarcaEquipo() { return marcaEquipo; }
    public void setMarcaEquipo(String marcaEquipo) { this.marcaEquipo = marcaEquipo; }

    public String getModeloEquipo() { return modeloEquipo; }
    public void setModeloEquipo(String modeloEquipo) { this.modeloEquipo = modeloEquipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public String getCondiciones() { return condiciones; }
    public void setCondiciones(String condiciones) { this.condiciones = condiciones; }

    public String getEntrega() { return entrega; }
    public void setEntrega(String entrega) { this.entrega = entrega; }

    public String getEntregadoPor() { return entregadoPor; }
    public void setEntregadoPor(String entregadoPor) { this.entregadoPor = entregadoPor; }

    public boolean isDevuelto() { return devuelto; }
    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
        this.status = calculateStatus();
    }

    public LocalDate getFechaDevuelto() { return fechaDevuelto; }
    public void setFechaDevuelto(LocalDate fechaDevuelto) { this.fechaDevuelto = fechaDevuelto; }

    public LocalTime getHoraDevuelto() { return horaDevuelto; }
    public void setHoraDevuelto(LocalTime horaDevuelto) { this.horaDevuelto = horaDevuelto; }

    public String getDevueltoPor() { return devueltoPor; }
    public void setDevueltoPor(String devueltoPor) { this.devueltoPor = devueltoPor; }

    public String getRecibidoPor() { return recibidoPor; }
    public void setRecibidoPor(String recibidoPor) { this.recibidoPor = recibidoPor; }

    public String getEstadoDevuelto() { return estadoDevuelto; }
    public void setEstadoDevuelto(String estadoDevuelto) { this.estadoDevuelto = estadoDevuelto; }

    public String getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(int diasRestantes) { this.diasRestantes = diasRestantes; }

    // Métodos auxiliares
    private String calculateStatus() {
        if (devuelto) {
            return "DEVUELTO";
        }

        if (fechaDevolucion != null) {
            LocalDate today = LocalDate.now();
            if (today.isAfter(fechaDevolucion)) {
                return "VENCIDO";
            } else if (today.isEqual(fechaDevolucion)) {
                return "VENCE_HOY";
            }
        }

        return "ACTIVO";
    }

    private int calculateDiasRestantes() {
        if (devuelto || fechaDevolucion == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        return (int) today.until(fechaDevolucion).getDays();
    }

    public String getEquipoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (nombreEquipo != null) sb.append(nombreEquipo);
        if (marcaEquipo != null && !marcaEquipo.isEmpty()) {
            sb.append(" - ").append(marcaEquipo);
        }
        if (modeloEquipo != null && !modeloEquipo.isEmpty()) {
            sb.append(" ").append(modeloEquipo);
        }
        return sb.toString();
    }

    public String getStatusColor() {
        switch (status) {
            case "ACTIVO": return "#388E3C";
            case "VENCIDO": return "#D32F2F";
            case "VENCE_HOY": return "#F57C00";
            case "DEVUELTO": return "#455A64";
            default: return "#455A64";
        }
    }

    public String getStatusText() {
        switch (status) {
            case "ACTIVO": return "Activo";
            case "VENCIDO": return "Vencido";
            case "VENCE_HOY": return "Vence Hoy";
            case "DEVUELTO": return "Devuelto";
            default: return "Desconocido";
        }
    }

    @Override
    public String toString() {
        return "PrestamoDTO{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", solicitanteNombre='" + solicitanteNombre + '\'' +
                ", nombreEquipo='" + nombreEquipo + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

