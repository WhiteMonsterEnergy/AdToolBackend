package dk.ek.adtoolbackend.ad;

import dk.ek.adtoolbackend.ad.dto.AdResponse;
import dk.ek.adtoolbackend.ad.dto.CreateAdRequest;
import dk.ek.adtoolbackend.ai.AiService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@CrossOrigin
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
        adService.ai();
        return ResponseEntity.ok("AdController is up and running!");
    }

    @GetMapping(value = "/ai/image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] image(@RequestParam String prompt) {
        String prompt1 = "A picturesque view of a mountain landscape during sunrise, with vibrant colors and a clear sky.";
        return aiService.generateImage(prompt);
    }

}
