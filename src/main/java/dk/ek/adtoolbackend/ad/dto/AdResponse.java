package dk.ek.adtoolbackend.ad.dto;

import java.time.LocalDateTime;

public class AdResponse {
    private Long id;
    private Long userId;
    private String imageRef;
    private LocalDateTime createdAt;

    public AdResponse() {}

    public AdResponse(Long id, Long userId, String imageRef, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.imageRef = imageRef;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getImageRef() { return imageRef; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setImageRef(String imageRef) { this.imageRef = imageRef; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
