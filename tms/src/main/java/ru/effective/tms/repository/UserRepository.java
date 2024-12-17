package ru.effective.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effective.tms.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * @param username {@link User} username.
     * @return Optional {@link User}.
     */
    Optional<User> findByUsername(String username);
    /**
     * @param email {@link User} email.
     * @return Optional {@link User}.
     */
    Optional<User> findByEmail(String email);

    /**
     * @param username {@link User} username.
     * @return true if {@link User} with username exists.
     */

    boolean existsByUsername(String username);

    /**
     * @param email {@link User} email.
     * @return true if {@link User} with parameters exist.
     */

    boolean existsByEmail(String email);

    /**
     * {@link #existsByEmail(String)}
     * exclude {@link User} where User.id equals id.
     *
     * @param email {@link User} email.
     * @param id    {@link User} id to exclude from the search.
     * @return true if {@link User} with parameters exist.
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * {@link #existsByUsername(String)}
     * exclude {@link User} where User.id equals id.
     *
     * @param username {@link User} username.
     * @param id       {@link User} id to exclude from the search.
     * @return true if {@link User} with parameters exist.
     */
    boolean existsByUsernameAndIdNot(String username, Long id);
}
