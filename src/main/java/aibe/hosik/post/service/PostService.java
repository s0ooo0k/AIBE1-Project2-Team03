package aibe.hosik.post.service;

import aibe.hosik.post.dto.PostDetailDTO;
import aibe.hosik.post.dto.PostRequestDTO;
import aibe.hosik.post.dto.PostResponseDTO;
import aibe.hosik.post.entity.Post;
import aibe.hosik.post.respository.PostRepository;
import aibe.hosik.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PostService {
  List<PostResponseDTO> getAllPosts();
  Post createPost(PostRequestDTO dto, User user);
  PostDetailDTO getPostDetail(Long postId);
}
