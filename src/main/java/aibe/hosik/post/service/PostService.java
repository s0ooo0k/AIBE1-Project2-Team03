package aibe.hosik.post.service;

import aibe.hosik.post.dto.PostDetailDTO;
import aibe.hosik.post.dto.PostRequestDTO;
import aibe.hosik.post.dto.PostResponseDTO;
import aibe.hosik.post.entity.Post;
import aibe.hosik.user.User;

import java.util.List;

public interface PostService {
  List<PostResponseDTO> getAllPosts();
  Post createPost(PostRequestDTO dto, User user);
  PostDetailDTO getPostDetail(Long postId);
}
