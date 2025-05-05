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

@Slf4j
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "모집글 API") // Swagger Tag
public class PostController {
  private final PostService postService;
  private final UserRepository userRepository;
  private final PostSkillRepository postSkillRepository;

  /**
   * 게시글을 등록하는 메서드입니다.
   *
   * @param dto 게시글 등록 요청 정보를 담고 있는 DTO 객체
   * @param user 인증된 사용자 정보
   * @return ResponseEntity 생성된 게시글에 대한 응답 객체
   */
  @Operation(summary="모집글 등록", description="모집글을 등록합니다.")
  @PostMapping
  public ResponseEntity<?> createPost(@RequestBody PostRequestDTO dto, @AuthenticationPrincipal User user){
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }
    Post createPost = postService.createPost(dto, user);
    // 스킬 조회
    List<String> skills = postSkillRepository.findSkillByPostId(createPost.getId());
    // dto 반환
    // TODO : currentCount 로직 구현 후 변환
    PostResponseDTO responseDTO = PostResponseDTO.from(createPost, skills, 0);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * 모든 모집글을 조회하는 메서드입니다.
   *
   * @return ResponseEntity로 감싸진 모집글 목록(PostResponseDTO 리스트)를 반환합니다.
   */
  @Operation(summary="모집글 조회", description = "모집글 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<List<PostResponseDTO>> getAllPosts(){
    return ResponseEntity.ok(postService.getAllPosts());
  }

  /**
   * 주어진 모집글 ID를 통해 모집글 상세 정보를 조회하는 메서드입니다.
   *
   * @param postId 상세 정보를 조회하고자 하는 모집글의 ID
   * @return ResponseEntity로 감싸진 모집글 상세 정보(PostDetailDTO 객체)를 반환합니다.
   */
  @Operation(summary="모집글 상세 조회", description="모집글 게시글을 상세 조회합니다")
  @GetMapping("/{postId}")
  public ResponseEntity<PostDetailDTO> getPostDetail(@PathVariable Long postId){
    return ResponseEntity.ok(postService.getPostDetail(postId));
  }

  /**
   * 주어진 게시글 ID를 사용하여 모집글을 삭제합니다.
   *
   * @param postId 삭제할 모집글의 ID
   * @param user 요청을 보낸 인증된 사용자 정보
   * @return 삭제 성공 시 204 No Content 응답
   */
  @Operation(summary="모집글 삭제", description ="작성자는 모집글을 삭제합니다")
  @DeleteMapping("/{postId}")
  public ResponseEntity<?> deletePost(@PathVariable Long postId, @AuthenticationPrincipal User user){
    postService.deletePost(postId, user);
    return ResponseEntity.noContent().build();
  }

  /**
   * 모집글을 수정하는 메서드입니다.
   *
   * @param postId 수정할 모집글 ID
   * @param dto    모집글 수정 정보를 담은 DTO
   * @param user   인증된 사용자 정보
   * @return 수정된 모집글의 데이터를 포함한 ResponseEntity
   */
  @Operation(summary="모집글 수정", description="모집글을 수정합니다.")
  @PatchMapping("/{postId}")
  public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                      @RequestBody PostPatchDTO dto,
                                      @AuthenticationPrincipal User user) {
    Post updatedPost = postService.updatePost(postId, dto, user);
    List<String> skills = postSkillRepository.findSkillByPostId(updatedPost.getId());
    // TODO : 현재 모집 인원 로직 추가 필요
    PostResponseDTO responseDTO = PostResponseDTO.from(updatedPost, skills, 0); 
    return ResponseEntity.ok(responseDTO);
  }


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
  @PatchMapping("/{postId}/mock")
  public ResponseEntity<?> updatePostForSwagger(@PathVariable Long postId,
                                                @RequestBody PostPatchDTO dto) {

    User mockUser = userRepository.findById(1L).orElseThrow();

    Post updatedPost = postService.updatePost(postId, dto, mockUser);
    List<String> skills = postSkillRepository.findSkillByPostId(updatedPost.getId());
    PostResponseDTO responseDTO = PostResponseDTO.from(updatedPost, skills, 0);
    return ResponseEntity.ok(responseDTO);
  }

  @Operation(summary="[로컬 TEST용] 모집글 삭제 테스트", description="[TEST] 테스트용 유저로 모집글을 삭제합니다.")
  @DeleteMapping("/{postId}/mock")
  public ResponseEntity<?> deletePostForSwagger(@PathVariable Long postId) {
    User mockUser = userRepository.findById(1L).orElseThrow();

    postService.deletePost(postId, mockUser);
    return ResponseEntity.noContent().build();
  }
}
