package ru.effective.tms.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import ru.effective.tms.model.enums.Priority;
import ru.effective.tms.model.enums.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldNameConstants
@Builder
@Entity
@Table(name = "tasks")
public class Task {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "current_status")
    @Enumerated(EnumType.STRING)
    private Status currentStatus;
    @Column(name = "current_priority")
    @Enumerated(EnumType.STRING)
    private Priority currentPriority;
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
    @ManyToMany(mappedBy = "takenTask")
    @Builder.Default
    private Set<User> performerList = new HashSet<>();
    @OneToMany(mappedBy = Comment.Fields.task, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && currentStatus == task.currentStatus
                && currentPriority == task.currentPriority
                && Objects.equals(author.getId(), task.author.getId())
                && Objects.equals(performerList.size(), task.performerList.size())
                && Objects.equals(comments.size(), task.comments.size());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, currentStatus, currentPriority);
    }
}
