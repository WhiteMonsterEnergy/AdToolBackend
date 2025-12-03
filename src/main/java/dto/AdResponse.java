package dto;

public class AdResponse {

    private String brandName;     // fx "Marmalade Co."
    private String headline;      // flere linjer med \n
    private String subline;       // "Kun i dag!!"
    private String discountText;  // "60% rabat"
    private String sizeInfo;      // "Cauan 193 cm bruger str. L."
    private String trustText;     // "4,8 stjerner på Trustpilot"

    // Getters + setters
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSubline() {
        return subline;
    }

    public void setSubline(String subline) {
        this.subline = subline;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public String getSizeInfo() {
        return sizeInfo;
    }

    public void setSizeInfo(String sizeInfo) {
        this.sizeInfo = sizeInfo;
    }

    public String getTrustText() {
        return trustText;
    }

    public void setTrustText(String trustText) {
        this.trustText = trustText;
    }
}
