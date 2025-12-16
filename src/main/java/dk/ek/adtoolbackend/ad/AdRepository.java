package dk.ek.adtoolbackend.ad;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdRepository extends JpaRepository<Ad, Long> {

    Optional<Ad> findByIdAndProfileId(Long id, int profileId);

    List<Ad> findAllByProfileId(int profileId);

    void deleteByIdAndProfileId(Long id, int profileId);
}
