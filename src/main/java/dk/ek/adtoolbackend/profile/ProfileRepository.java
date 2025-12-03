/* this repository does the following:
Basic CRUD functions from JPARepository
Custom query to find profile by name (used for login)
*/


package dk.ek.adtoolbackend.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;



public interface ProfileRepository extends JpaRepository<Profile, Integer> {
Optional<Profile> findByName(String name);
}