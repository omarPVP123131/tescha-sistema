package tescha.configuracion.dto;

public class ConfiguracionDTO {
    private int id;
    private boolean respaldosAutomaticos;
    private String frecuenciaRespaldo;
    private String rutaRespaldo;

    // Constructores, getters y setters
    public ConfiguracionDTO() {}

    public ConfiguracionDTO(boolean respaldosAutomaticos, String frecuenciaRespaldo, String rutaRespaldo) {
        this.respaldosAutomaticos = respaldosAutomaticos;
        this.frecuenciaRespaldo = frecuenciaRespaldo;
        this.rutaRespaldo = rutaRespaldo;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isRespaldosAutomaticos() { return respaldosAutomaticos; }
    public void setRespaldosAutomaticos(boolean respaldosAutomaticos) { this.respaldosAutomaticos = respaldosAutomaticos; }

    public String getFrecuenciaRespaldo() { return frecuenciaRespaldo; }
    public void setFrecuenciaRespaldo(String frecuenciaRespaldo) { this.frecuenciaRespaldo = frecuenciaRespaldo; }

    public String getRutaRespaldo() { return rutaRespaldo; }
    public void setRutaRespaldo(String rutaRespaldo) { this.rutaRespaldo = rutaRespaldo; }
}