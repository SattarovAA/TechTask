package ru.effective.tms.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.effective.tms.model.dto.TaskFilter;
import ru.effective.tms.model.entity.Task;
import ru.effective.tms.model.entity.User;

public interface TaskSpecifications {
    /**
     * Specification with filter.
     *
     * @param filter {@link TaskFilter} with the required parameters
     * @return Specification with the required parameters
     */
    static Specification<Task> withFilter(TaskFilter filter) {
        return Specification
                .where(byLongField(Task.Fields.id,
                        filter.getId())
                )
                .and(byStringField(Task.Fields.title,
                        filter.getTitle())
                )
                .and(byStringField(Task.Fields.description,
                        filter.getDescription())
                )
                .and(byStringField(Task.Fields.currentStatus,
                        filter.getCurrentStatus())
                )
                .and(byStringField(Task.Fields.currentPriority,
                        filter.getCurrentPriority())
                )
                .and(byAuthorId(filter.getAuthorId()))
                .and(byPerformerId(filter.getPerformerId())
                );
    }

    /**
     * Entity with {@link Long} type field equals criterion.
     *
     * @param field     field name in entity {@link Task}.
     * @param criterion searched value.
     * @return Specification with the required parameters.
     */
    static Specification<Task> byLongField(String field, Long criterion) {
        return ((root, query, criteriaBuilder) -> {
            if (criterion == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(field), criterion);
        });
    }

    /**
     * Task where {@link Task}.author.id field equals authorId.
     *
     * @param authorId User id from Task.author.
     * @return Specification with the required parameters.
     * @see User
     */
    static Specification<Task> byAuthorId(Long authorId) {
        return ((root, query, criteriaBuilder) -> {
            if (authorId == null) {
                return null;
            }
            return criteriaBuilder.equal(
                    root.get(Task.Fields.author).get(User.Fields.id),
                    authorId
            );
        });
    }

    /**
     * Task where {@link Task}.author.id field equals authorId.
     *
     * @param criterion task with criterion id in performerList.
     * @return Specification with the required parameters.
     * @see User
     */
    static Specification<Task> byPerformerId(Long criterion) {
        return ((root, query, criteriaBuilder) -> {
            if (criterion == null) {
                return null;
            }
            return criteriaBuilder.equal(
                    root.join(Task.Fields.performerList).get(User.Fields.id),
                    criterion
            );
        });
    }

    /**
     * Entity with {@link String} type field equals criterion.
     *
     * @param field     field name in entity {@link Task}.
     * @param criterion searched value.
     * @return Specification with the required parameters.
     */
    static Specification<Task> byStringField(String field, String criterion) {
        return ((root, query, criteriaBuilder) -> {
            if (criterion == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(field), criterion);
        });
    }
}
