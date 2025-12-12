package dk.ek.adtoolbackend.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ImageGenerationResponse {

    public static class Data {
        @JsonProperty("b64_json")
        private String b64Json;

        public String getB64Json() { return b64Json; }
        public void setB64Json(String b64Json) { this.b64Json = b64Json; }
    }

    private List<Data> data;

    public List<Data> getData() { return data; }
    public void setData(List<Data> data) { this.data = data; }
}
