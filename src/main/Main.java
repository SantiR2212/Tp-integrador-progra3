package main;

import main.models.Diagnosis;
import main.models.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {

    // Rutas (asumiendo que 'data' está al mismo nivel que 'java_inference_engine')
    private static final String FACTS_PATH = "./data/facts.json";
    private static final String RULES_RESOURCE_PATH = "rules.yaml"; // Se lee desde 'resources'
    private static final String REPORT_JSON_PATH = "./data/report.json";
    private static final String REPORT_TXT_PATH = "./data/report.txt";

    public static void main(String[] args) {
        System.out.println("Iniciando Motor de Inferencia Java...");
        InferenceEngine engine = new InferenceEngine();

        try {
            // 1. Leer hechos [cite: 78]
            Map<String, Boolean> facts = engine.loadFacts(FACTS_PATH);
            System.out.println("Hechos cargados: " + facts);

            // 2. Leer conocimiento [cite: 81]
            List<Rule> rules = engine.loadRules(RULES_RESOURCE_PATH);
            System.out.println("Reglas cargadas: " + rules.size());

            // 3. Inferir [cite: 83]
            List<Diagnosis> finalDiagnoses = engine.evaluate(facts, rules);

            // 4. y 5. Guardar y Explicar [cite: 86, 89]
            engine.saveReport(finalDiagnoses, REPORT_JSON_PATH, REPORT_TXT_PATH);

            System.out.println("--- Reporte Generado en " + REPORT_TXT_PATH + " ---");
            // Opcional: Imprimir el reporte txt a la consola
            Files.lines(Paths.get(REPORT_TXT_PATH)).forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error durante la ejecución del motor de inferencia:");
            e.printStackTrace();
        }
    }
}
