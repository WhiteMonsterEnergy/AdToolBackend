package dk.ek.adtoolbackend.ad;

import dk.ek.adtoolbackend.ad.dto.AdResponse;
import dk.ek.adtoolbackend.ad.dto.CreateAdRequest;
import dk.ek.adtoolbackend.ai.AiService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
@RequestMapping("api/ads")
public class AdController {

    private final AdService adService;
    private final AiService aiService;

    public AdController(AdService adService, AiService aiService) {
        this.aiService = aiService;
        this.adService = adService;
    }

    @PostMapping
    public ResponseEntity<?> createAd(
            @RequestHeader(value = "X-Profile-Id", required = false) Integer profileId,
            @RequestBody CreateAdRequest request
    ) {
        try {
            if (profileId == null) {
                return ResponseEntity.badRequest().body("X-Profile-Id header is required");
            }

            Ad saved = adService.saveAdForProfile(profileId, request.getImageRef());

            AdResponse body = new AdResponse(
                    saved.getId(),
                    saved.getProfileId(),
                    saved.getImageRef(),
                    saved.getCreatedAt()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "api/ads/" + saved.getId());
            return new ResponseEntity<>(body, headers, HttpStatus.CREATED);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save generated ad.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAd(
            @PathVariable Long id,
            @RequestHeader(value = "X-Profile-Id", required = false) Integer profileId
    ) {
        if (profileId == null) {
            return ResponseEntity.badRequest().body("X-Profile-Id header is required");
        }

        boolean deleted = adService.deleteAd(id, profileId);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("AdController is up and running!");
    }

    /**
     * Genererer et billede.
     *
     * Du kan bruge den på 2 måder:
     * 1) Send prompt direkte: /api/ads/ai/image?prompt=...
     * 2) Send felter: /api/ads/ai/image?brand=...&headline=...&subline=...&discountText=...&trustText=...&modelInfo=...
     */
    @GetMapping(value = "/ai/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> image(
            @RequestParam(required = false) String prompt,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String headline,
            @RequestParam(required = false) String subline,
            @RequestParam(required = false) String discountText,
            @RequestParam(required = false) String trustText,
            @RequestParam(required = false) String modelInfo
    ) {
        byte[] png;

        // Hvis prompt er sendt, så brug den.
        // Ellers bygger vi prompten ud fra felterne (brand/trust/modelinfo osv.)
        if (prompt != null && !prompt.isBlank()) {
            png = aiService.generateImage(prompt);
        } else {
            // Gode defaults så endpoint ikke crasher hvis frontend mangler noget
            String b = (brand == null || brand.isBlank()) ? "Marmalade Co." : brand;
            String h = (headline == null || headline.isBlank()) ? "LAGERSALG PÅ\nUDVALGTE\nPRODUKTER" : headline;
            String s = (subline == null || subline.isBlank()) ? "Kun i dag!!" : subline;
            String d = (discountText == null || discountText.isBlank()) ? "60% rabat" : discountText;
            String t = (trustText == null || trustText.isBlank()) ? "4,8 stjerner på Trustpilot" : trustText;
            String m = (modelInfo == null || modelInfo.isBlank()) ? "Modellen er 172 cm og bærer str. S" : modelInfo;

            png = aiService.generateAdImage(b, h, s, d, t, m);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noStore()) // så du ikke bliver snydt af caching mens du tester
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ad.png\"")
                .body(png);
    }
}
