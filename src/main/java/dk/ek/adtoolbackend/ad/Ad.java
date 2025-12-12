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
    @Column(name = "profile_id")
    private int profileId;

    /* Generated image reference (URL or path) */
    @Column(name = "image_ref")
    private String imageRef;

    /* Creation timestamp */
    @Column(name = "created_at")
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

    public Long getId() { return id; }

    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    public String getImageRef() { return imageRef; }
    public void setImageRef(String imageRef) { this.imageRef = imageRef; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
