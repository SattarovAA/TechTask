package ru.effective.tms.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskFilter {
    /**
     * Minimum size of the page field.
     */
    @Value("app.filter.task.minPageSize")
    private static final int MIN_PAGE_SIZE = 1;
    /**
     * Maximum size of the page field.
     */
    @Value("app.filter.task.maxPageSize")
    private static final int MAX_PAGE_SIZE = 20;

    @Min(value = MIN_PAGE_SIZE,
            message = "Поле pageSize не должно быть меньше " + MIN_PAGE_SIZE)
    @Max(value = MAX_PAGE_SIZE,
            message = "Поле pageSize не должно быть больше " + MAX_PAGE_SIZE)
    @NotNull(message = "Поле pageSize должно быть заполнено")
    private Integer pageSize;
    @PositiveOrZero(message = "Поле pageNumber должно быть положительным")
    @NotNull(message = "Поле pageNumber должно быть заполнено")
    private Integer pageNumber;

    private Long id;
    private String title;
    private String description;
    private String currentStatus;
    private String currentPriority;
    private Long authorId;
    private Long performerId;
}
