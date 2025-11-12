package main.models;

import java.util.List;

// Esta clase también necesita getters y setters para Jackson
public class Diagnosis {
    private String diagnostico;
    private List<String> acciones;
    private String explicacion;
    private int peso;

    public Diagnosis(String diagnostico, List<String> acciones, String explicacion, int peso) {
        this.diagnostico = diagnostico;
        this.acciones = acciones;
        this.explicacion = explicacion;
        this.peso = peso;
    }

    // --- Getters y Setters ---
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public List<String> getAcciones() { return acciones; }
    public void setAcciones(List<String> acciones) { this.acciones = acciones; }
    public String getExplicacion() { return explicacion; }
    public void setExplicacion(String explicacion) { this.explicacion = explicacion; }
    public int getPeso() { return peso; }
    public void setPeso(int peso) { this.peso = peso; }
}
