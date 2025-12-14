package dk.ek.adtoolbackend.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageGenerationRequest {

    private String model;
    private String prompt;
    private String size;
    private Integer n;

    @JsonProperty("output_format")
    private String outputFormat; // png/jpeg/webp (gpt-image-1)

    public String getModel() { return model; }
    public String getPrompt() { return prompt; }
    public String getSize() { return size; }
    public Integer getN() { return n; }
    public String getOutputFormat() { return outputFormat; }

    public void setModel(String model) { this.model = model; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public void setSize(String size) { this.size = size; }
    public void setN(Integer n) { this.n = n; }
    public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
}
