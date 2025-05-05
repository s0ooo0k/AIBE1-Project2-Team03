package aibe.hosik.comment.controller;

import aibe.hosik.comment.dto.CommentRequestDTO;
import aibe.hosik.comment.dto.CommentResponseDTO;
import aibe.hosik.comment.service.CommentService;
import aibe.hosik.comment.entity.Comment;
import aibe.hosik.user.User;
import aibe.hosik.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[Mock] 댓글 테스트 API", description = "Swagger 테스트용 인증 없는 댓글 등록/조회/삭제용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments/mock")
public class CommentMockController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    @Operation(summary = "[로컬 TEST용] 댓글 등록", description = "[TEST] mock user로 댓글을 등록합니다.")
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentRequestDTO dto) {
        User mockUser = userRepository.findById(2L)
                .orElseThrow();

        commentService.createComment(dto, mockUser);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "[로컬 TEST용] 댓글 목록 조회", description = "특정 게시글의 댓글을 트리 구조로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(@RequestParam Long postId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "[로컬 TEST용] 댓글 삭제", description = "[TEST] mock user (id=2)로 본인이 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        User mockUser = userRepository.findById(2L)
                .orElseThrow();

        commentService.deleteComment(commentId, mockUser);
        return ResponseEntity.noContent().build();
    }
}
