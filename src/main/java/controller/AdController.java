package controller;

import dto.AdRequest;
import dto.AdResponse;
import service.AdGeneratorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ads")
@CrossOrigin(origins = "http://localhost:5173") // ret til din frontend-port
public class AdController {

    private final AdGeneratorService adGeneratorService;

    public AdController(AdGeneratorService adGeneratorService) {
        this.adGeneratorService = adGeneratorService;
    }

    @PostMapping("/generate")
    public AdResponse generateAd(@RequestBody AdRequest request) {
        return adGeneratorService.generate(request);
    }
}
