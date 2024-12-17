package ru.effective.tms.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import ru.effective.tms.model.entity.security.RoleType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@FieldNameConstants
@Builder
@Entity
@Table(name = "users",
        indexes = {
                @Index(columnList = User.Fields.username, unique = true),
                @Index(columnList = User.Fields.email, unique = true)
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.id)
    private Long id;
    @Column(name = Fields.username)
    private String username;
    @Column(name = Fields.password)
    private String password;
    @Column(name = Fields.email)
    private String email;
    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<RoleType> roles = new HashSet<>();
    @OneToMany(mappedBy = Task.Fields.author, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Task> createdTask = new ArrayList<>();
    @ManyToMany()
    @JoinTable(name = "user_task",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "performer_id"))
    @Builder.Default
    private Set<Task> takenTask = new HashSet<>();
    @OneToMany(mappedBy = Comment.Fields.author, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}

