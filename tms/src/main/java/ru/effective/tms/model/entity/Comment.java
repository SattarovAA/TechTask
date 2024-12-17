package ru.effective.tms.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldNameConstants
@Builder
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "content")
    private String content;
    @CreationTimestamp
    @Column(name = "creation_time")
    private Instant creationTime;
    @UpdateTimestamp
    @Column(name = "updated_time")
    private Instant updatedTime;
    @ManyToOne()
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;
}
