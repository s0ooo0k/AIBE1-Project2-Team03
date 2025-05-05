package aibe.hosik.post.controller;

import aibe.hosik.post.dto.PostDetailDTO;
import aibe.hosik.post.dto.PostPatchDTO;
import aibe.hosik.post.dto.PostRequestDTO;
import aibe.hosik.post.dto.PostResponseDTO;
import aibe.hosik.post.entity.Post;
import aibe.hosik.post.service.PostService;
import aibe.hosik.skill.repository.PostSkillRepository;
import aibe.hosik.user.User;
import aibe.hosik.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "[Mock] 모집글 테스트 API", description = "Swagger 테스트용 인증 없는 모집글 CRUD API")
@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostMockController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final PostSkillRepository postSkillRepository;

    // ========= Swagger 테스트 mock
    @Operation(summary="[로컬 TEST용] 모집글 등록 테스트", description="[TEST] 모집글을 등록합니다.")
    @PostMapping("/mock")
    public ResponseEntity<?> createPostForSwagger(@RequestBody PostRequestDTO dto){

        // 테스트용 userId
        User mockUser = userRepository.findById(1L).orElseThrow();
        Post createPost = postService.createPost(dto, mockUser);
        List<String> skills = postSkillRepository.findSkillByPostId(createPost.getId());
        // TODO : 현재 모집 인원 로직 추가 필요
        PostResponseDTO responseDTO = PostResponseDTO.from(createPost, skills, 0);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary="[로컬 TEST용] 모집글 수정 테스트", description="[TEST] 모집글을 수정합니다.")
    @PatchMapping("/mock/{postId}")
    public ResponseEntity<?> updatePostForSwagger(@PathVariable Long postId,
                                                  @RequestBody PostPatchDTO dto) {

        User mockUser = userRepository.findById(1L).orElseThrow();

        Post updatedPost = postService.updatePost(postId, dto, mockUser);
        List<String> skills = postSkillRepository.findSkillByPostId(updatedPost.getId());
        PostResponseDTO responseDTO = PostResponseDTO.from(updatedPost, skills, 0);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary="[로컬 TEST용] 모집글 삭제 테스트", description="[TEST] 테스트용 유저로 모집글을 삭제합니다.")
    @DeleteMapping("/mock/{postId}")
    public ResponseEntity<?> deletePostForSwagger(@PathVariable Long postId) {
        User mockUser = userRepository.findById(1L).orElseThrow();

        postService.deletePost(postId, mockUser);
        return ResponseEntity.noContent().build();
    }
}
