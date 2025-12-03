package service;

import dto.AdRequest;
import dto.AdResponse;
import org.springframework.stereotype.Service;

@Service
public class AdGeneratorService {

    public AdResponse generate(AdRequest request) {
        // TODO: Senere kan du bruge request.getPrompt() til at lave rigtige AI-calls

        AdResponse res = new AdResponse();

        res.setBrandName("Marmalade A/S");
        res.setHeadline("LAGERSALG PÅ\nUDVALGTE\nPRODUKTER");
        res.setSubline("Kun i dag!!");
        res.setDiscountText("60% rabat");
        res.setSizeInfo("Cauan 193 cm bruger str. L.");
        res.setTrustText("4,8 stjerner på Trustpilot");

        return res;
    }
}
