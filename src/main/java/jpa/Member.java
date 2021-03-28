package jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
//@Table(name = "USER") // Table명 변경
@Getter
@Setter
public class Member {

  @Id
  private Long id;
  private String name;
}
