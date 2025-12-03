/*
as a Repository class, it handles the database operations by working in between the Controller and the database.
technically part of the model layer in the MVC structure.
does the following:
- basic CRUD functions from JPARepository
- custom query to find profile by name (used for login)
*/


package dk.ek.adtoolbackend.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;



public interface ProfileRepository extends JpaRepository<Profile, Integer> {
Optional<Profile> findByName(String name);
}