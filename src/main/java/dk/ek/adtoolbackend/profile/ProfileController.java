/*
as a controller, it handles HTTP requests, working in between the view and model in the MVC structure.
does the following:
- profile login + logout
- profile creation
no business logic should be here - it should instead call methods from the service layer
*/


package dk.ek.adtoolbackend.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;


    }


    @PostMapping("/register")
public ResponseEntity<Profile> createNewProfile(@RequestBody Profile profile) {

        // ensures that neither the profile object itself, nor name or passwordHash are null
if (profile == null || profile.getName() == null || profile.getPasswordHash() == null) {
    return ResponseEntity.badRequest().body(null);
}

profile = profileService.createProfile(profile);
        if (profile == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(profile);


    }


    @PostMapping("/login")
    public ResponseEntity<Profile> loginToProfile(@RequestBody Profile profile) {


        // ensures that neither the profile object itself, nor name or passwordHash are null
        if (profile == null || profile.getName() == null || profile.getPasswordHash() == null) {
            return ResponseEntity.badRequest().build();
        }

        Profile authenticated = profileService.authenticateAndGetProfile(profile.getName(), profile.getPasswordHash());
        if (authenticated != null){
            return ResponseEntity.ok(authenticated);
        } else {
            return ResponseEntity.status(401).build();

        }
    }
}

