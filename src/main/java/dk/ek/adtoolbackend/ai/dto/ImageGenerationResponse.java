package dk.ek.adtoolbackend.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ImageGenerationResponse {

    private Long created;
    private String background;

    @JsonProperty("output_format")
    private String outputFormat;

    private String quality;
    private String size;

    private List<Data> data;
    private Usage usage;

    /* ===== getters / setters ===== */

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    /* ===== nested classes ===== */

    public static class Data {

        @JsonProperty("b64_json")
        private String b64Json;

        public String getB64Json() {
            return b64Json;
        }

        public void setB64Json(String b64Json) {
            this.b64Json = b64Json;
        }
    }

    public static class Usage {

        @JsonProperty("input_tokens")
        private Integer inputTokens;

        @JsonProperty("output_tokens")
        private Integer outputTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;

        @JsonProperty("input_tokens_details")
        private InputTokensDetails inputTokensDetails;

        public Integer getInputTokens() {
            return inputTokens;
        }

        public void setInputTokens(Integer inputTokens) {
            this.inputTokens = inputTokens;
        }

        public Integer getOutputTokens() {
            return outputTokens;
        }

        public void setOutputTokens(Integer outputTokens) {
            this.outputTokens = outputTokens;
        }

        public Integer getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }

        public InputTokensDetails getInputTokensDetails() {
            return inputTokensDetails;
        }

        public void setInputTokensDetails(InputTokensDetails inputTokensDetails) {
            this.inputTokensDetails = inputTokensDetails;
        }
    }

    public static class InputTokensDetails {

        @JsonProperty("image_tokens")
        private Integer imageTokens;

        @JsonProperty("text_tokens")
        private Integer textTokens;

        public Integer getImageTokens() {
            return imageTokens;
        }

        public void setImageTokens(Integer imageTokens) {
            this.imageTokens = imageTokens;
        }

        public Integer getTextTokens() {
            return textTokens;
        }

        public void setTextTokens(Integer textTokens) {
            this.textTokens = textTokens;
        }
    }
}
