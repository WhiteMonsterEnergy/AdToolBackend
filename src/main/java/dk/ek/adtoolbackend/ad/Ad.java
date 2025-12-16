package dk.ek.adtoolbackend.ad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Profile-based ownership */
    @Column(name = "profile_id", nullable = false)
    private int profileId;

    /* Generated image reference (valgfri – kan bruges senere til fil/URL) */
    @Column(name = "image_ref")
    private String imageRef;

    /* 🔴 SELVE BILLEDET (BLOB) */
    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    /* Creation timestamp */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Ad() {}

    public Ad(int profileId, String imageRef) {
        this.profileId = profileId;
        this.imageRef = imageRef;
    }

    /* -------- getters / setters -------- */

    public Long getId() {
        return id;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
