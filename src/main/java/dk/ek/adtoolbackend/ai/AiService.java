package dk.ek.adtoolbackend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ek.adtoolbackend.ai.dto.ChatCompletionRequest;
import dk.ek.adtoolbackend.ai.dto.ChatCompletionResponse;
import dk.ek.adtoolbackend.ai.dto.ImageGenerationRequest;
import dk.ek.adtoolbackend.ai.dto.ImageGenerationResponse;
import dk.ek.adtoolbackend.ai.dto.MyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Base64;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Value("${app.api-key}") private String apiKey;
    @Value("${app.url}")     private String chatUrl;
    @Value("${app.model}")   private String model;
    @Value("${app.temperature}") private double temperature;

    private final WebClient client = WebClient.builder()
            .exchangeStrategies(
                    ExchangeStrategies.builder()
                            .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
                            .build()
            )
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

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
     * Bygger en "ad-template" prompt, så du får brand + trust + modelinfo med i billedet.
     * Hold den her samlet ét sted, så du nemt kan tweake layout senere.
     */
    public String buildAdImagePrompt(
            String brand,
            String headline,
            String subline,
            String discountText,
            String trustText,
            String modelInfo
    ) {
        // headline kan komme ind med \n fra frontend – vi vil faktisk gerne have line breaks i prompten
        String safeBrand = brand == null ? "" : brand.trim();
        String safeHeadline = headline == null ? "" : headline.trim();
        String safeSubline = subline == null ? "" : subline.trim();
        String safeDiscount = discountText == null ? "" : discountText.trim();
        String safeTrust = trustText == null ? "" : trustText.trim();
        String safeModelInfo = modelInfo == null ? "" : modelInfo.trim();

        return """
                Create a high-quality fashion advertisement image for women aged 18–30.

                Branding:
                - Display the brand name "%s" clearly in the top-right corner
                - Use a clean, modern sans-serif font for the brand name

                Main headline text (large, bold, uppercase, left-aligned):
                "%s"

                Subline text (smaller, italic or script-style font, under the headline):
                "%s"

                Discount badge:
                - A solid red or dark pink rectangular badge
                - White bold text inside the badge:
                "%s"

                Trustpilot rating:
                - Include a small Trustpilot-style rating element near the bottom
                - Show green stars and the text:
                "%s"

                Model information:
                - Add a subtle caption bar at the bottom of the image
                - Text:
                "%s"

                Visual style:
                - Pink and red color palette
                - Modern, clean fashion advertisement
                - Confident young woman as the model
                - Studio lighting, soft shadows
                - Instagram / webshop promotional style
                - High-end e-commerce look

                Composition:
                - Balanced layout with text on the left and model on the right
                - Clear visual hierarchy: headline → discount → brand → trust elements
                """.formatted(
                safeBrand,
                safeHeadline,
                safeSubline,
                safeDiscount,
                safeTrust,
                safeModelInfo
        );
    }

    /**
     * Convenience-metode: bygger prompt ud fra dine felter og genererer billed-bytes.
     * Den her er nice at kalde fra controlleren.
     */
    public byte[] generateAdImage(
            String brand,
            String headline,
            String subline,
            String discountText,
            String trustText,
            String modelInfo
    ) {
        String prompt = buildAdImagePrompt(brand, headline, subline, discountText, trustText, modelInfo);
        log.info("Generating ad image with prompt length: {}", prompt.length());
        return generateImage(prompt);
    }

    /**
     * Genererer et billede og returnerer rå PNG-bytes.
     */
    public byte[] generateImage(String prompt) {
        try {
            ImageGenerationRequest req = new ImageGenerationRequest();
            req.setModel("gpt-image-1");
            req.setPrompt(prompt);
            req.setSize("1024x1024");
            req.setN(1);
            req.setOutputFormat("png");

            String raw = client.post()
                    .uri("https://api.openai.com/v1/images/generations")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (raw == null || raw.isBlank()) {
                throw new IllegalStateException("OpenAI returned 200 but empty body");
            }

            ImageGenerationResponse response = objectMapper.readValue(raw, ImageGenerationResponse.class);

            if (response.getData() == null || response.getData().isEmpty()
                    || response.getData().get(0).getB64Json() == null) {
                throw new IllegalStateException("OpenAI returned JSON but missing data[0].b64_json");
            }

            return Base64.getDecoder().decode(response.getData().get(0).getB64Json());

        } catch (WebClientResponseException e) {
            log.error("OpenAI image error {}: {}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI image call failed. Check logs.");
        } catch (Exception e) {
            log.error("Unexpected image error", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error. Check logs.");
        }
    }
}
