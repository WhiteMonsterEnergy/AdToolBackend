package dk.ek.adtoolbackend.ad.dto;

import java.time.LocalDateTime;

public class AdResponse {
    private Long id;
    private int profileId;
    private String imageRef;
    private LocalDateTime createdAt;

    public AdResponse() {}

    public AdResponse(Long id, int profileId, String imageRef, LocalDateTime createdAt) {
        this.id = id;
        this.profileId = profileId;
        this.imageRef = imageRef;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public int getProfileId() { return profileId; }
    public String getImageRef() { return imageRef; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setImageRef(String imageRef) { this.imageRef = imageRef; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
