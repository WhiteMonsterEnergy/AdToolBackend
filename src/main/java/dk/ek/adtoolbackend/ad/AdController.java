package dk.ek.adtoolbackend.ad;

import dk.ek.adtoolbackend.ad.dto.AdResponse;
import dk.ek.adtoolbackend.ad.dto.CreateAdRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
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
}
