package aibe.hosik.post.service;

import aibe.hosik.post.dto.MatchedUserDTO;
import aibe.hosik.post.dto.PostDetailDTO;
import aibe.hosik.post.dto.PostRequestDTO;
import aibe.hosik.post.dto.PostResponseDTO;
import aibe.hosik.post.entity.Post;
import aibe.hosik.post.respository.PostRepository;
import aibe.hosik.skill.repository.PostSkillRepository;
import aibe.hosik.skill.repository.SkillRepository;
import aibe.hosik.skill.entity.PostSkill;
import aibe.hosik.skill.entity.Skill;
import aibe.hosik.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
  private final PostRepository postRepository;
  private final SkillRepository skillRepository;
  private final PostSkillRepository postSkillRepository;

  // PostResponseDTO를 통해 전체 게시글(Post)를 조회합니다
  @Override
  public List<PostResponseDTO> getAllPosts() {
    List<Post> posts = postRepository.findAll();

    return posts.stream()
            .map(post -> {
              // post와 연관된 스킬 이름 조회
              List<String> skills = postSkillRepository.findSkillByPostId(post.getId());

              // TODO : 현재 참여자 수 계산
              Integer currentCount = 0;

              // DTO 정적 팩토리 메서드 활용
              return PostResponseDTO.from(post, skills, currentCount);
            }).collect(Collectors.toList());
  }

  // 주어진 PostRequestDTO와 User를 기반으로 새로운 게시글(Post)을 생성하고 저장합니다.
  // 요청된 스킬 리스트를 기준으로 스킬을 찾거나 새로 생성하여 Post와 연결합니다.
  @Override
  public Post createPost(PostRequestDTO dto, User user) {
    // toEntity 사용해서 Post 객체 생성
    Post post = dto.toEntity(user);
    // 생성한 객체 Post 저장
    Post savePost = postRepository.save(post);

    // Skill 찾거나 생성
    for(String skillName : dto.skills()) {
      Skill skill = skillRepository.findByName(skillName)
              .orElseGet(() -> skillRepository.save(Skill.builder().name(skillName).build()));

      PostSkill postSkill = PostSkill.builder()
              .post(savePost)
              .skill(skill)
              .build();

      // post-skill 연관관계 추가
      postSkillRepository.save(postSkill);
    }
    return savePost;
  }

  @Override
  public PostDetailDTO getPostDetail(Long postId) {
    // 게시글 정보 조회
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));

    // 스킬 이름 조회
    List<String> skills = postSkillRepository.findSkillByPostId(postId);

    // 매칭 사용자 정보 조회
    // TODO : 실제 매칭된 사용자 조회
    List<MatchedUserDTO> matchedUsers = new ArrayList<>();

    return PostDetailDTO.from(post,skills, matchedUsers);
  }
}
