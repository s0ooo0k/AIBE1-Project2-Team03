package aibe.hosik.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column
  private boolean isProgress;

  @Column
  private String image;

  @Column(nullable = false)
  private LocalDate endedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostCategory category;
}
