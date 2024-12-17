package ru.effective.tms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.effective.tms.exception.DeleteEntityWithReferenceException;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.repository.UserRepository;
import ru.effective.tms.service.UserService;
import ru.effective.tms.exception.AlreadyExitsException;
import ru.effective.tms.exception.EntityNotFoundException;

import java.text.MessageFormat;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * {@link User} Repository.
     */
    private final UserRepository userRepository;
    /**
     * Default page size.
     *
     * @see #findAll()
     */
    @Value("${app.service.user.defaultPageSize}")
    private int defaultPageSize = 10;
    /**
     * Default page number.
     *
     * @see #findAll()
     */
    @Value("${app.service.user.defaultPageNumber}")
    private int defaultPageNumber;
    /**
     * Default {@link PasswordEncoder}.
     * Needed to define and update the field password in {@link User}.
     *
     * @see #encode(User)
     * @see #insert(User, Long)
     */
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        log.info("Try to get all users without filter.");
        return userRepository.findAll(
                PageRequest.of(defaultPageNumber, defaultPageSize)
        ).getContent();
    }

    @Override
    public User findById(Long id) {
        log.info("find user with id {}.", id);
        return userRepository.findById(id).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format(
                                "User with id {0} not found!",
                                id
                        )
                )
        );
    }

    @Override
    public User findByUsername(String username) {
        log.info("find user with username {}.", username);
        return userRepository.findByUsername(username).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format(
                                "User with username {0} not found!",
                                username
                        )
                )
        );
    }
    @Override
    public User findByEmail(String email) {
        log.info("find user with email {}.", email);
        return userRepository.findByEmail(email).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format(
                                "User with email {0} not found!",
                                email
                        )
                )
        );
    }

    @Override
    public User save(User model) {
        log.warn("Try to create new user.");
        encode(model);
        checkDuplicateUsername(model.getUsername());
        checkDuplicateEmail(model.getEmail());
        return userRepository.save(model);
    }

    @Override
    public User update(Long id, User model) {
        log.warn("Try to update user with id {}.", id);
        User updatedUser = insert(model, id);
        checkDuplicateUsername(updatedUser.getUsername(), updatedUser.getId());
        checkDuplicateEmail(updatedUser.getEmail(), updatedUser.getId());
        return userRepository.save(updatedUser);
    }

    /**
     * Encode password for model.
     *
     * @param model {@link User} model with password to encode.
     */
    private void encode(User model) {
        log.warn("Try to encode pass for entity with id {}.", model.getId());
        model.setPassword(passwordEncoder.encode(model.getPassword()));
    }

    /**
     * Update model to full version.
     * If the model has no field values, then the values are taken
     * from a previously existing entity with the same id.
     *
     * @param model   {@link User} with partially updated fields.
     * @param modelId user id to update {@link User}.
     * @return Updated {@link User}.
     */
    private User insert(User model, Long modelId) {
        User userToUpdate = findById(modelId);
        return User.builder()
                .id(modelId)
                .username(model.getUsername() == null
                        ? userToUpdate.getUsername()
                        : model.getUsername())
                .password(model.getPassword() == null
                        ? passwordEncoder.encode(userToUpdate.getPassword())
                        : passwordEncoder.encode(model.getPassword()))
                .roles(model.getRoles() == null
                        ? userToUpdate.getRoles()
                        : model.getRoles())
                .email(model.getEmail() == null
                        ? userToUpdate.getEmail()
                        : model.getEmail())
                .createdTask(model.getCreatedTask() == null
                        ? userToUpdate.getCreatedTask()
                        : model.getCreatedTask())
                .takenTask(model.getTakenTask() == null
                        ? userToUpdate.getTakenTask()
                        : model.getTakenTask())
                .comments(model.getComments() == null
                        ? userToUpdate.getComments()
                        : model.getComments())
                .build();
    }

    /**
     * Check duplicate username.
     * For save new {@link User}.
     *
     * @param username username to check.
     * @throws AlreadyExitsException if username already exist.
     */
    private void checkDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Username ({0}) already exist!",
                            username
                    )
            );
        }
    }

    /**
     * Check duplicate username.
     * For update {@link User}.
     *
     * @param username      username to check.
     * @param currentUserId current user id.
     * @throws AlreadyExitsException if username already exist
     *                               excluding {@link User} with currentUserId.
     */
    private void checkDuplicateUsername(String username, Long currentUserId) {
        if (userRepository.existsByUsernameAndIdNot(username, currentUserId)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Username ({0}) already exist!",
                            username
                    )
            );
        }
    }

    /**
     * Check duplicate email.
     * For save new {@link User}.
     *
     * @param email email to check.
     * @throws AlreadyExitsException if email already exist.
     */
    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Email ({0}) already exist!",
                            email
                    )
            );
        }
    }

    /**
     * Check duplicate email.
     * For update {@link User}.
     *
     * @param email         email to check.
     * @param currentUserId current user id.
     * @throws AlreadyExitsException if email already exist
     *                               excluding {@link User} with currentUserId.
     */
    private void checkDuplicateEmail(String email, Long currentUserId) {
        if (userRepository.existsByEmailAndIdNot(email, currentUserId)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Email ({0}) already exist!",
                            email
                    )
            );
        }
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Try to delete user with id {}.", id);
        User userToDelete = findById(id);
        checkUserReference(userToDelete);
        userRepository.deleteById(id);
    }

    private void checkUserReference(User model) {
        checkUserCommentsReference(model);
        checkUserCreatedTaskReference(model);
        checkUserTakenTaskReference(model);
    }

    private void checkUserCommentsReference(User model) {
        if (!model.getComments().isEmpty()) {
            throw new DeleteEntityWithReferenceException(
                    MessageFormat.format(
                            "Unable to delete user with id {0}. User have {1} comments.",
                            model.getId(),
                            model.getComments().size()
                    )
            );
        }
    }

    private void checkUserCreatedTaskReference(User model) {
        if (!model.getCreatedTask().isEmpty()) {
            throw new DeleteEntityWithReferenceException(
                    MessageFormat.format(
                            "Unable to delete room with id {0}. User have {1} created task.",
                            model.getId(),
                            model.getCreatedTask().size()
                    )
            );
        }
    }

    private void checkUserTakenTaskReference(User model) {
        if (!model.getTakenTask().isEmpty()) {
            throw new DeleteEntityWithReferenceException(
                    MessageFormat.format(
                            "Unable to delete room with id {0}. User have {1} taken task.",
                            model.getId(),
                            model.getTakenTask().size()
                    )
            );
        }
    }


    @Override
    public void existsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    MessageFormat.format(
                            "User with id {0} not found!",
                            id
                    )
            );
        }
    }
}
