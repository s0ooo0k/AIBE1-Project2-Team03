package aibe.hosik.post.controller;

import aibe.hosik.post.dto.PostDetailDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 테스트
@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "모집글 API") // Swagger Tag
public class PostController {
  private final PostService postService;
  private final UserRepository userRepository;
  private final PostSkillRepository postSkillRepository;

  @Operation(summary="모집글 등록", description="모집글을 등록합니다.")
  @PostMapping
  public ResponseEntity<?> createPost(@RequestBody PostRequestDTO dto, @AuthenticationPrincipal User user){
    Post createPost = postService.createPost(dto, user);
    // 스킬 조회
    List<String> skills = postSkillRepository.findSkillByPostId(createPost.getId());
    // dto 반환
    // TODO : currentCount 로직 구현 후 변환
    PostResponseDTO responseDTO = PostResponseDTO.from(createPost, skills, 0);
    return ResponseEntity.ok(responseDTO);
  }
  // 테스트용
  @Operation(summary="모집글 등록 테스트", description="[TEST] 모집글을 등록합니다.")
  @PostMapping("/mock")
  public ResponseEntity<?> createPostForSwagger(@RequestBody PostRequestDTO dto){

    // 테스트용 userId
    User mockUser = userRepository.findById(1L).orElseThrow();
    Post createPost = postService.createPost(dto, mockUser);
    List<String> skills = postSkillRepository.findSkillByPostId(createPost.getId());
    PostResponseDTO responseDTO = PostResponseDTO.from(createPost, skills, 0);
    return ResponseEntity.ok(responseDTO);
  }

  @Operation(summary="모집글 조회", description = "모집글 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<List<PostResponseDTO>> getAllPosts(){
    return ResponseEntity.ok(postService.getAllPosts());
  }

  @Operation(summary="모집글 상세 조회", description="모집글 게시글을 상세 조회합니다")
  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailDTO> getPostDetail(@PathVariable Long postId){
    return ResponseEntity.ok(postService.getPostDetail(postId));
  }
}
