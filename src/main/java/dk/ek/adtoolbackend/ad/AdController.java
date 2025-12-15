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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping(value = "/save-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveGeneratedImage(
            @RequestHeader(value = "X-Profile-Id", required = false) Integer profileId,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            if (profileId == null) {
                return ResponseEntity.badRequest().body("X-Profile-Id header is required");
            }
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest().body("image is required");
            }

            byte[] bytes = image.getBytes();
            System.out.println("Uploading image bytes: " + bytes.length);

            Ad saved = adService.saveAdForProfile(profileId, bytes);

            return ResponseEntity.ok(saved.getId());

        } catch (Exception ex) {
            ex.printStackTrace(); // <- den her er guld lige nu
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save generated image: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
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

        if (prompt != null && !prompt.isBlank()) {
            png = aiService.generateImage(prompt);
        } else {
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
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ad.png\"")
                .body(png);
    }
}
