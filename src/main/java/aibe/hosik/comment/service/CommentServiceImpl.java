package aibe.hosik.comment.service;

import aibe.hosik.comment.dto.CommentRequestDTO;
import aibe.hosik.comment.dto.CommentResponseDTO;
import aibe.hosik.comment.entity.Comment;
import aibe.hosik.comment.repository.CommentRepository;
import aibe.hosik.post.entity.Post;
import aibe.hosik.post.repository.PostRepository;
import aibe.hosik.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public void createComment(CommentRequestDTO dto, User user) {
        // post id로 게시글 찾기
        Post post = postRepository.findById(dto.postId())
                .orElseThrow();

        // 대댓글일 경우 부모 댓글 확인
        Comment parent = null;
        if(dto.parentCommentId() != null) {
            parent = commentRepository.findById(dto.parentCommentId())
                    .orElseThrow();
        }

        // 댓글 생성 - 엔티티 변환
        Comment comment = dto.toEntity(post, user, parent);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        // postId의 댓글 모두 찾기
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        Map<Long, CommentResponseDTO> commentMap = new HashMap<>();
        List<CommentResponseDTO> roots = new ArrayList<>();

        // 댓글 DTO 변환 > Map에 저장
        for(Comment c : comments) {
            // DTO 변환
            CommentResponseDTO dto = CommentResponseDTO.from(c);
            commentMap.put(c.getId(), dto);

            if(c.getParentComment() == null) {
                roots.add(dto);
            } else {
                // 부모가 있으면 부모의 replies에 추가
                CommentResponseDTO parentDto = commentMap.get(c.getParentComment().getId());
                parentDto.replies().add(dto);
            }
        }
        return roots;
    }

}
