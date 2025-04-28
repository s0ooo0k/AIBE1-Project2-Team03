package aibe.hosik.user;

import aibe.hosik.profile.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column
  private String socialId;

//  @Column
//  private Type socialType;
//  TODO 2곳 이상의 social login 구현 시 필요

  @OneToOne(mappedBy = "user")
  private Profile profile;
}
