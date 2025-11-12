package main;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import main.models.Diagnosis;
import main.models.Rule;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class InferenceEngine {

    private final ObjectMapper jsonMapper;

    public InferenceEngine() {
        // Configura Jackson para "pretty print" en los reportes JSON
        this.jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * ETAPA 1: Lee los hechos desde facts.json
     */
    public Map<String, Boolean> loadFacts(String factsPath) throws IOException {
        File factsFile = new File(factsPath);
        // Lee el JSON directamente a un Mapa
        TypeReference<HashMap<String, Boolean>> typeRef = new TypeReference<>() {};
        return jsonMapper.readValue(factsFile, typeRef);
    }

    /**
     * ETAPA 2: Carga la base de conocimiento desde rules.yaml
     */
    /**
     * ETAPA 2: Carga la base de conocimiento desde rules.yaml
     */
    public List<Rule> loadRules(String rulesResourcePath) {
        // 1. Creamos un Yaml simple, SIN el Constructor(Rule.class)
        Yaml yaml = new Yaml(new LoaderOptions());

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(rulesResourcePath);

        // 2. Cargamos el YAML como una lista genérica de Mapas.
        //    Usamos .load() (singular), NO .loadAll()
        List<Map<String, Object>> yamlData = yaml.load(inputStream);

        // 3. Usamos Jackson (jsonMapper) para convertir esos Mapas a objetos Rule
        List<Rule> rules = new ArrayList<>();
        for (Map<String, Object> entry : yamlData) {
            // convertValue es un metodo de Jackson para esta conversión
            try {
                Rule rule = jsonMapper.convertValue(entry, Rule.class);
                rules.add(rule);
            } catch (Exception e) {
                System.err.println("Error al convertir la regla: " + entry.get("id"));
                e.printStackTrace();
            }
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rules;
    }

    /**
     * ETAPA 3: Evalúa los hechos contra las reglas (Inferencia)
     */
    public List<Diagnosis> evaluate(Map<String, Boolean> facts, List<Rule> rules) {
        List<Diagnosis> triggeredDiagnoses = new ArrayList<>();

        for (Rule rule : rules) {
            boolean ruleTriggered = checkRuleConditions(rule, facts);

            if (ruleTriggered) {
                // Se dispara la regla. Generamos el diagnóstico y la explicación
                String explanation = buildExplanation(rule, facts);
                Diagnosis diagnosis = new Diagnosis(
                        rule.getDiagnostico() + " (" + rule.getId() + ")",
                        rule.getAcciones(),
                        explanation,
                        rule.getPeso()
                );
                triggeredDiagnoses.add(diagnosis);
            }
        }

        // ETAPA 4 (parcial): Ordena las conclusiones por importancia (peso)
        triggeredDiagnoses.sort(Comparator.comparingInt(Diagnosis::getPeso).reversed());

        return triggeredDiagnoses;
    }

    /**
     * Verifica si todas las condiciones de una regla se cumplen con los hechos
     */
    private boolean checkRuleConditions(Rule rule, Map<String, Boolean> facts) {
        for (Map.Entry<String, Boolean> condition : rule.getConditions().entrySet()) {
            String factName = condition.getKey();
            boolean expectedValue = condition.getValue();

            // Si el hecho no está presente O el valor no coincide, la regla no se activa
            if (!facts.getOrDefault(factName, false) == expectedValue) {
                return false;
            }
        }
        return true; // Todas las condiciones se cumplieron
    }

    /**
     * Construye la explicación en formato "R1: no_arranca=true y ..."
     */
    private String buildExplanation(Rule rule, Map<String, Boolean> facts) {
        String conditionsStr = rule.getConditions().entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), facts.get(entry.getKey())))
                .collect(Collectors.joining(" y "));

        return String.format("%s: %s => regla activada", rule.getId(), conditionsStr);
    }

    /**
     * ETAPA 5: Genera los reportes (JSON y TXT) [cite: 59, 89]
     */
    public void saveReport(List<Diagnosis> diagnoses, String jsonPath, String txtPath) throws IOException {
        // Guardar report.json
        jsonMapper.writeValue(new File(jsonPath), diagnoses);

        // Guardar report.txt [cite: 64-74]
        try (PrintWriter writer = new PrintWriter(new FileWriter(txtPath))) {
            if (diagnoses.isEmpty()) {
                writer.println("No se encontraron diagnósticos.");
                return;
            }

            writer.println("Diagnósticos Encontrados:");
            for (Diagnosis d : diagnoses) {
                writer.println("- " + d.getDiagnostico());
            }

            writer.println("\nRecomendaciones:");
            for (Diagnosis d : diagnoses) {
                for (String accion : d.getAcciones()) {
                    writer.println("- " + accion);
                }
            }

            writer.println("\nExplicación:");
            for (Diagnosis d : diagnoses) {
                writer.println("- " + d.getExplicacion());
            }
        }
    }
}
