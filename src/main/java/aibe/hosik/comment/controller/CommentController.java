package aibe.hosik.comment.controller;

import aibe.hosik.comment.dto.CommentRequestDTO;
import aibe.hosik.comment.dto.CommentResponseDTO;
import aibe.hosik.comment.entity.Comment;
import aibe.hosik.comment.service.CommentService;
import aibe.hosik.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {
  private final CommentService commentService;

  /**
   * 댓글 또는 대댓글을 등록하는 메서드입니다.
   *
   * @param dto 댓글 등록 요청 데이터를 담는 객체
   * @param user 현재 인증된 사용자의 정보를 담고 있는 객체
   * @return HTTP 상태가 CREATED인 ResponseEntity 객체
   */
  @Operation(summary="댓글 등록", description="댓글 및 대댓글을 등록합니다.")
  @PostMapping
  public ResponseEntity<?> createComment(@RequestBody CommentRequestDTO dto, @AuthenticationPrincipal User user) {
    commentService.createComment(dto, user);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * 특정 게시글의 모든 댓글 및 대댓글을 조회하는 메서드입니다.
   *
   * @param postId 댓글을 조회할 게시글의 ID
   * @return 댓글과 대댓글 정보를 담은 CommentResponseDTO 객체의 리스트를 포함한 ResponseEntity
   */
  @Operation(summary="댓글 조회", description=" 모든 댓글 및 대댓글을 조회합니다.")
  @GetMapping
  public ResponseEntity<List<CommentResponseDTO>> getComments(@RequestParam Long postId){
    List<CommentResponseDTO> comments = commentService.getCommentsByPostId(postId);
    return ResponseEntity.ok(comments);
  }

  /**
   * 댓글을 삭제하는 메서드입니다.
   *
   * @param commentId 삭제하려는 댓글의 ID
   * @param user 현재 인증된 사용자의 정보를 담고 있는 객체
   * @return HTTP 상태가 NO_CONTENT인 ResponseEntity 객체
   */
  @Operation(summary="댓글 삭제", description="댓글을 삭제합니다.")
  @DeleteMapping("/{commentId}")
  public ResponseEntity<?> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal User user) {
    commentService.deleteComment(commentId, user);
    return ResponseEntity.noContent().build();
  }
}
