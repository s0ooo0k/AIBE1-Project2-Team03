package aibe.hosik.post.entity;

import aibe.hosik.common.TimeEntity;
import aibe.hosik.skill.entity.PostSkill;
import aibe.hosik.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Post extends TimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column
  @Builder.Default
  private boolean isDone = false;

  @Column
  private int headCount;

  @Column
  private String image;

  @Column
  private String requirementPersonality;

  @Column(nullable = false)
  private LocalDate endedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostCategory category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostType type;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;
  
  // 양방향 매핑
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostSkill> postSkills = new ArrayList<>();
}
