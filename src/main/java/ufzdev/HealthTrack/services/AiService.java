package ufzdev.HealthTrack.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import ufzdev.HealthTrack.dao.SystemConfigDao;
import ufzdev.HealthTrack.dao.SystemConfigFirestoreDao;
import ufzdev.HealthTrack.models.SystemConfigModel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class AiService {
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final SystemConfigDao configDao;
    private final HttpClient httpClient;
    private final Gson gson;

    public AiService() {
        this.configDao = new SystemConfigFirestoreDao();
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Envía una consulta a la IA configurada (OpenAI por defecto según el usuario).
     * @param systemPrompt El contexto o rol de la IA (Doctor, Asistente, etc.)
     * @param userPrompt La consulta específica del usuario o datos de salud.
     * @return CompletableFuture con la respuesta de la IA.
     */
    public CompletableFuture<String> askAi(String systemPrompt, String userPrompt) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SystemConfigModel config = configDao.get();
                String apiKey = config.getAiApiKey();

                if (apiKey == null || apiKey.isEmpty()) {
                    throw new Exception("API Key no configurada. Por favor, ve a Configuración.");
                }

                // Construir el cuerpo de la petición para OpenAI
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", "gpt-3.5-turbo"); // O gpt-4 si se prefiere
                
                JsonArray messages = new JsonArray();
                
                JsonObject systemMsg = new JsonObject();
                systemMsg.addProperty("role", "system");
                systemMsg.addProperty("content", systemPrompt);
                messages.add(systemMsg);
                
                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", userPrompt);
                messages.add(userMsg);
                
                requestBody.add("messages", messages);
                requestBody.addProperty("temperature", 0.7);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OPENAI_URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new Exception("Error de OpenAI (Status " + response.statusCode() + "): " + response.body());
                }

                // Parsear respuesta
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                return jsonResponse.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    // Helpers para diferentes roles
    
    public CompletableFuture<String> getDoctorAnalysis(String patientData) {
        String systemPrompt = "Eres un asistente médico experto. Analiza los siguientes datos de salud de un paciente y proporciona un resumen clínico, posibles riesgos y recomendaciones profesionales.";
        return askAi(systemPrompt, patientData);
    }

    public CompletableFuture<String> getPatientAdvice(String metrics) {
        String systemPrompt = "Eres un asistente de salud preventivo. Explica estos resultados de forma sencilla al paciente y dale consejos motivadores para mejorar su estilo de vida.";
        return askAi(systemPrompt, metrics);
    }

    public CompletableFuture<String> getAdminReport(String systemStats) {
        String systemPrompt = "Eres un analista de datos de sistemas de salud. Genera un reporte ejecutivo basado en estas estadísticas de uso y actividad del sistema.";
        return askAi(systemPrompt, systemStats);
    }
}
