package dk.ek.adtoolbackend.ad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ads")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* --- Added for US3 --- */

    // (User ID) the owner of this ad
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // (Image reference) where the generated image is stored (path or URL)
    @Column(name = "image_ref", nullable = false)
    private String imageRef;

    // (Created date) when this ad was created
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* --- Lifecycle hooks for createdAt --- */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /* --- Constructors --- */
    public Ad() {}

    public Ad(Long userId, String imageRef) {
        this.userId = userId;
        this.imageRef = imageRef;
    }

    /* --- Getters & Setters --- */
    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getImageRef() { return imageRef; }
    public void setImageRef(String imageRef) { this.imageRef = imageRef; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
