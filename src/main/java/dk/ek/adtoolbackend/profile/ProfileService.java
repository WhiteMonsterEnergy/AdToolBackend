/*
this service class handles business logic related to Profile entities
this keeps the controller and repository layers clean
- checks for existing profile with the same name before creating a new profile
- returns null if profile with the same name already exists
- creates and saves new profile if name is unique
*/

package dk.ek.adtoolbackend.profile;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;


@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

public ProfileService(ProfileRepository profileRepository) {
this.profileRepository = profileRepository;
}

public Profile createProfile(Profile profile) {

    if (profileRepository.findByName(profile.getName()).isPresent()) {
        return null;
    } else {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(profile.getPasswordHash());
        profile.setPasswordHash(hashedPassword);
        return profileRepository.save(profile);}
}

public Profile authenticateAndGetProfile(String name, String passwordHash) {

    // find profile by name in ProfileRepository
    Optional<Profile> optionalProfile = profileRepository.findByName(name);

    // if empty return null
    if (optionalProfile.isEmpty()) {
        return null;

    } else {
        // get profile object
        Profile profile = optionalProfile.get();

        // get hash from profile
        String storedHash = profile.getPasswordHash();




// check if raw input password matches stored hash
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(passwordHash, storedHash);


        // if passwordHash matches the storedHash return profile, else return null
        if (matches) {
            return profile;
            } else {
            return null;
        }
}

}

}
