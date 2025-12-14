package dk.ek.adtoolbackend.ad;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public void ai() {

    }

}
