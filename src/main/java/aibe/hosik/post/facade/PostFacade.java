package aibe.hosik.post.facade;

import aibe.hosik.post.dto.PostDetailDTO;
import aibe.hosik.post.dto.PostPatchDTO;
import aibe.hosik.post.dto.PostRequestDTO;
import aibe.hosik.post.dto.PostResponseDTO;
import aibe.hosik.post.entity.Post;
import aibe.hosik.post.service.PostService;
import aibe.hosik.skill.repository.PostSkillRepository;
import aibe.hosik.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostFacade {
    private final PostService postService;
    private final PostSkillRepository postSkillRepository;

    /**
     * 주어진 요청 데이터와 사용자 정보를 기반으로 새로운 게시글을 생성합니다.
     *
     * @param dto 게시글 생성을 위한 요청 데이터가 포함된 객체
     * @param user 게시글을 생성하는 사용자 정보
     * @return 생성된 게시글의 내용을 담고 있는 응답 DTO
     */
    public PostResponseDTO createPost(PostRequestDTO dto, User user) {
        Post created = postService.createPost(dto, user);
        List<String> skills = postSkillRepository.findSkillByPostId(created.getId());
        int currentCount = 0; // TODO: 참여자 수 계산 로직 필요
        return PostResponseDTO.from(created, skills, currentCount);
    }

    /**
     * 모든 게시글의 목록을 반환합니다.
     *
     * @return 모든 게시글의 정보를 담고 있는 PostResponseDTO 리스트
     */
    public List<PostResponseDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    /**
     * 특정 게시글의 상세 정보를 반환합니다.
     *
     * @param postId 상세*/
    public PostDetailDTO getPostDetail(Long postId) {
        return postService.getPostDetail(postId); // 이미 내부에서 matchedUsers까지 포함
    }

    /**
     * 주어진 게시글 ID와 사용자 정보를 기반으로 해당 게시글을 삭제합니다.
     *
     * @param postId 삭제할 게시글의 ID
     * @param user 삭제 작업을 요청하는 사용자 정보
     */
    public void deletePost(Long postId, User user) {
        postService.deletePost(postId, user);
    }

    /**
     * 주어진 게시글 ID와 수정 요청 데이터를 바탕으로 게시글을 업데이트합니다.
     * 업데이트된 게시글 정보를 기반으로 응답 DTO를 생성하여 반환합니다.
     *
     * @param postId 업데이트할 게시글의 ID
     * @param dto 게시글 업데이트에 필요한 데이터를 포함하는 DTO
     * @param user 수정 작업을 요청하는 사용자 정보
     * @return 업데이트된 게시글 정보를 포함하는 PostResponseDTO
     */
    public PostResponseDTO updatePost(Long postId, PostPatchDTO dto, User user) {
        Post updated = postService.updatePost(postId, dto, user);
        List<String> skills = postSkillRepository.findSkillByPostId(updated.getId());
        // TODO: 참여자 수 계산 로직 필요
        int currentCount = 0;
        return PostResponseDTO.from(updated, skills, currentCount);
    }
}
