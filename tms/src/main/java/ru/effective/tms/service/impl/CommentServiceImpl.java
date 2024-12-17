package ru.effective.tms.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.effective.tms.exception.EntityNotFoundException;
import ru.effective.tms.model.entity.Comment;
import ru.effective.tms.model.entity.User;
import ru.effective.tms.repository.CommentRepository;
import ru.effective.tms.service.CommentService;
import ru.effective.tms.service.TaskService;
import ru.effective.tms.service.UserService;

import java.text.MessageFormat;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    /**
     * {@link Comment} Repository.
     */
    private final CommentRepository commentRepository;
    /**
     * Service for working with users.
     */
    private final UserService userService;
    /**
     * Service for working with tasks.
     */
    private final TaskService taskService;
    /**
     * Default page size.
     *
     * @see #findAll()
     */
    @Value("${app.service.comment.defaultPageSize}")
    private int defaultPageSize = 10;
    /**
     * Default page number.
     *
     * @see #findAll()
     */
    @Value("${app.service.comment.defaultPageNumber}")
    private int defaultPageNumber;

    @Override
    public List<Comment> findAll() {
        log.info("Try to get all comments without filter.");
        return commentRepository.findAll(
                PageRequest.of(defaultPageNumber, defaultPageSize)
        ).getContent();
    }

    @Override
    public Comment findById(Long id) {
        log.info("find comment with id {}.", id);
        return commentRepository.findById(id).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format(
                                "Comment with id {0} not found!",
                                id
                        )
                )
        );
    }

    @Override
    public Comment save(Comment model) {
        log.warn("Try to create new comment.");
        taskService.existsById(model.getTask().getId());
        Comment modelWithAuthor = addAuthorToCommentFromPrincipal(model);
        return commentRepository.save(modelWithAuthor);
    }

    private Comment addAuthorToCommentFromPrincipal(Comment model) {
        UserDetails principal = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        String username = principal.getUsername();
        User author = userService.findByUsername(username);
        log.warn("add author with username {} to comment from principal.", username);
        author.getComments().add(model);
        userService.update(author.getId(), author);
        model.setAuthor(author);
        return model;
    }

    @Override
    public Comment update(Long id, Comment model) {
        log.warn("Try to update comment with id: " + id);
        findById(id);
        model.setId(id);
        return commentRepository.save(model);
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Try to delete comment with id {}.", id);
        existsById(id);
        commentRepository.deleteById(id);
    }

    private void existsById(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    MessageFormat.format(
                            "Comment with id {0} not found!",
                            id
                    )
            );
        }
    }
}
