package dk.ek.adtoolbackend.ad;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdService {

    private final AdRepository adRepository;

    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    /**
     * Creates and persists a generated ad.
     * @param userId   owner of the ad (must be provided by auth layer)
     * @param imageRef URL or file path to the generated image (jpg expected by acceptance criteria)
     * @return persisted Ad
     * @throws IllegalArgumentException when inputs are invalid
     */
    public Ad createGeneratedAd(Long userId, String imageRef) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (!StringUtils.hasText(imageRef)) {
            throw new IllegalArgumentException("imageRef is required");
        }
        // Optional tiny check for jpg expectation (you may relax/adjust later)
        if (!(imageRef.toLowerCase().endsWith(".jpg") || imageRef.toLowerCase().endsWith(".jpeg"))) {
            // not blocking by default; comment the next line if you want to allow other formats
            throw new IllegalArgumentException("imageRef must point to a .jpg/.jpeg image");
        }

        Ad ad = new Ad(userId, imageRef);
        return adRepository.save(ad);
    }
}
