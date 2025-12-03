package dto;

public class AdRequest {

    // fx: "Lagersalg på skjorter til unge mænd, 60% rabat, seriøs men frisk"
    private String prompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
