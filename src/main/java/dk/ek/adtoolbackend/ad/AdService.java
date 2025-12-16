package dk.ek.adtoolbackend.ad;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class AdService {

    private final AdRepository adRepository;

    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    public Ad createGeneratedAd(int profileId, String imageRef) {
        if (!StringUtils.hasText(imageRef)) {
            throw new IllegalArgumentException("imageRef is required");
        }
        Ad ad = new Ad(profileId, imageRef);
        return adRepository.save(ad);
    }

    public boolean deleteAd(Long adId, int profileId) {
        return adRepository.findByIdAndProfileId(adId, profileId)
                .map(ad -> {
                    adRepository.delete(ad);
                    return true;
                })
                .orElse(false);
    }

    public Ad saveAdForProfile(int profileId, String imageRef) {
        Ad ad = new Ad(profileId, imageRef);
        return adRepository.save(ad);
    }

    public Ad saveAdForProfile(int profileId, byte[] imageData) {
        Ad ad = new Ad(profileId, null);
        ad.setImageData(imageData);
        return adRepository.save(ad);
    }

    public Optional<Ad> findByIdAndProfileId(Long id, int profileId) {
        return adRepository.findByIdAndProfileId(id, profileId);
    }

    public void ai() {

    }
}
