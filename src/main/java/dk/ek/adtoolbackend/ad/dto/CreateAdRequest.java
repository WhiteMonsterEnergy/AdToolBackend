package dk.ek.adtoolbackend.ad.dto;

public class CreateAdRequest {
    // Reference to already-generated image (URL or file path)
    private String imageRef;

    public CreateAdRequest() {}

    public String getImageRef() { return imageRef; }
    public void setImageRef(String imageRef) { this.imageRef = imageRef; }
}
