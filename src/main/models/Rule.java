package main.models;

import java.util.List;
import java.util.Map;

// Esta clase debe tener getters y setters para que SnakeYAML funcione
public class Rule {
    private String id;
    private int peso;
    private Map<String, Boolean> conditions;
    private String diagnostico;
    private List<String> acciones;

    // --- Getters y Setters (Necesarios para la librería) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getPeso() { return peso; }
    public void setPeso(int peso) { this.peso = peso; }
    public Map<String, Boolean> getConditions() { return conditions; }
    public void setConditions(Map<String, Boolean> conditions) { this.conditions = conditions; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public List<String> getAcciones() { return acciones; }
    public void setAcciones(List<String> acciones) { this.acciones = acciones; }
}