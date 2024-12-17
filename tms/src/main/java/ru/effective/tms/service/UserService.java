package ru.effective.tms.service;

import ru.effective.tms.model.entity.User;

public interface UserService extends CrudService<User> {
    /**
     * Find {@link User} by string username.
     *
     * @param username {@link User} username.
     * @return {@link User} with searched username.
     */
    User findByUsername(String username);

    /**
     * Find {@link User} by string email.
     *
     * @param email {@link User} email.
     * @return {@link User} with searched email.
     */
    User findByEmail(String email);

    /**
     * Check {@link User} existence by id.
     *
     * @param id id to find.
     */
    void existsById(Long id);
}
