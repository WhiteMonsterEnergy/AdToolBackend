/*
this service class handles business logic related to Profile entities
this keeps the controller and repository layers clean
- checks for existing profile with the same name before creating a new profile
- returns null if profile with the same name already exists
- creates and saves new profile if name is unique
*/

package dk.ek.adtoolbackend.profile;

import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

public ProfileService(ProfileRepository profileRepository) {
this.profileRepository = profileRepository;
}

public Profile createProfile(Profile profile) {

    if (profileRepository.findByName(profile.getName()).isPresent()) {
        return null;
    } else {return profileRepository.save(profile);}
}

}
