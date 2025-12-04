package dk.ek.adtoolbackend.ad;

import dk.ek.adtoolbackend.ad.dto.AdResponse;
import dk.ek.adtoolbackend.ad.dto.CreateAdRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    /**
     * POST /ads
     * Saves a generated ad (stores imageRef + userId + createdAt).
     * Assumes user id is provided via X-User-Id header (temporary stand-in for auth).
     */
    @PostMapping
    public ResponseEntity<?> createAd(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody CreateAdRequest request
    ) {
        try {
            Ad saved = adService.createGeneratedAd(userId, request.getImageRef());

            AdResponse body = new AdResponse(
                    saved.getId(),
                    saved.getUserId(),
                    saved.getImageRef(),
                    saved.getCreatedAt()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/ads/" + saved.getId());
            return new ResponseEntity<>(body, headers, HttpStatus.CREATED);

        } catch (IllegalArgumentException ex) {
            // Bad input (e.g., missing header or imageRef)
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            // Unexpected failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save generated ad.");
        }
    }
}
