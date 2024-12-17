package ru.effective.tms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.effective.tms.model.dto.comment.CommentListResponse;
import ru.effective.tms.model.dto.comment.CommentRequest;
import ru.effective.tms.model.dto.comment.CommentResponse;
import ru.effective.tms.model.entity.Comment;

import java.util.List;

/**
 * Mapper for working with {@link Comment} entity.
 *
 * @see CommentResponse
 * @see CommentRequest
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    /**
     * {@link CommentRequest} to {@link Comment} mapping.
     *
     * @param request {@link CommentRequest} for mapping.
     * @return mapped {@link Comment}.
     */
    @Mapping(source = "taskId", target = "task.id")
    Comment requestToModel(CommentRequest request);

    /**
     * {@link Comment} to {@link CommentResponse} mapping.
     *
     * @param model {@link Comment} for mapping.
     * @return mapped {@link CommentResponse}.
     */
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "author.username", target = "userName")
    CommentResponse modelToResponse(Comment model);

    /**
     * List of {@link Comment} to List of {@link CommentResponse} mapping.
     *
     * @param comments List of {@link Comment} for mapping.
     * @return mapped List of {@link CommentResponse}.
     */
    List<CommentResponse> modelListToModelResponseList(List<Comment> comments);

    /**
     * List of {@link Comment} to {@link CommentListResponse} mapping.
     *
     * @param comments List of {@link Comment} for mapping.
     * @return mapped {@link CommentListResponse}.
     * @see #modelListToModelResponseList(List)
     */
    default CommentListResponse modelListToModelListResponse(List<Comment> comments) {
        CommentListResponse response = new CommentListResponse();
        response.setComments(modelListToModelResponseList(comments));
        return response;
    }
}
