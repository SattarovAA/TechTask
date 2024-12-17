package ru.effective.tms.service;

import java.util.List;

/**
 * Default CRUD interface service for working with entity {@link T}.
 *
 * @param <T> entity for work.
 */
public interface CrudService<T> {
    /**
     * Find all objects of type {@link T}.
     *
     * @return all objects of type {@link T}.
     */
    List<T> findAll();

    /**
     * Find object of type {@link T} where T.id equals id.
     *
     * @param id searched T.id value.
     * @return object of type {@link T} with searched id.
     */
    T findById(Long id);

    /**
     * Save object model of type {@link T}.
     *
     * @param model object of type {@link T} to save.
     * @return object of type {@link T} that was saved.
     */
    T save(T model);

    /**
     * Update object model of type {@link T} with T.id equals id.
     *
     * @param id    id of the object to be updated.
     * @param model object of type {@link T} to update.
     * @return object of type {@link T} that was updated.
     */
    T update(Long id, T model);

    /**
     * Delete object with T.id equals id from database.
     *
     * @param id id of the object to be deleted.
     */
    void deleteById(Long id);
}
