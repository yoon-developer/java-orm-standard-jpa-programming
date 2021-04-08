package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Table(name = "USER") // Table명 변경
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {

  @Id
  @GeneratedValue()
  private Long id;

  @Column(name = "username", nullable = false, length = 10)
  private String name;

  private Integer age;

  @Enumerated(EnumType.STRING)
  private RoleType roleType;

  @Lob
  private String description;

}
