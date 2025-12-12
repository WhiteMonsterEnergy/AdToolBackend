package dk.ek.adtoolbackend.ai;

import dk.ek.adtoolbackend.ai.dto.ChatCompletionRequest;
import dk.ek.adtoolbackend.ai.dto.ChatCompletionResponse;
import dk.ek.adtoolbackend.ai.dto.MyResponse;
import dk.ek.adtoolbackend.ai.dto.ImageGenerationRequest;
import dk.ek.adtoolbackend.ai.dto.ImageGenerationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Base64;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Value("${app.api-key}") private String apiKey;

    // Denne bruger du til chat (samme som nu)
    @Value("${app.url}")     private String chatUrl;

    @Value("${app.model}")   private String model;
    @Value("${app.temperature}") private double temperature;

    private final WebClient client = WebClient.create();

    public MyResponse makeRequest(String userPrompt, String systemMessage) {
        try {
            ChatCompletionRequest req = new ChatCompletionRequest();
            req.setModel(model);
            req.setTemperature(temperature);
            req.getMessages().add(new ChatCompletionRequest.Message("system", systemMessage));
            req.getMessages().add(new ChatCompletionRequest.Message("user", userPrompt));

            ChatCompletionResponse response = client.post()
                    .uri(new URI(chatUrl))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(req))
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty response from OpenAI");
            }

            String answer = response.getChoices().get(0).getMessage().getContent().trim();
            return new MyResponse(answer);

        } catch (WebClientResponseException e) {
            log.error("OpenAI chat error {}: {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "External AI chat call failed. Check logs.");
        } catch (Exception e) {
            log.error("Unexpected chat error", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error. Check logs.");
        }
    }

    /**
     * Genererer et billede og returnerer rå PNG-bytes.
     * Tip: returnér dem direkte fra en controller med produces = IMAGE_PNG_VALUE
     */
    public byte[] generateImage(String prompt) {
        try {
            ImageGenerationRequest req = new ImageGenerationRequest();
            req.setModel("gpt-image-1");
            req.setPrompt(prompt);
            req.setSize("1024x1024");
            req.setN(1);
            req.setOutputFormat("png");

            ImageGenerationResponse response = client.post()
                    .uri(new URI("https://api.openai.com/v1/images/generations"))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(ImageGenerationResponse.class)
                    .block();

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty image response from OpenAI");
            }

            String b64 = response.getData().get(0).getB64Json();
            return Base64.getDecoder().decode(b64);

        } catch (WebClientResponseException e) {
            // Den her linje er guld når du debugger 400’ere:
            log.error("OpenAI image error {}: {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "External AI image call failed. Check logs.");
        } catch (Exception e) {
            log.error("Unexpected image error", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error. Check backend logs.");
        }
    }


}
