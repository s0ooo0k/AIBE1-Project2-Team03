package aibe.hosik.post.entity;

import aibe.hosik.common.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

  @Column(nullable = false)
  private LocalDate endedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostCategory category;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostType type;
}
