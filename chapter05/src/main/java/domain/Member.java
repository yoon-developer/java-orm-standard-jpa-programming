package domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

  @Id
  @GeneratedValue()
  @Column(name = "MEMBER_ID")
  private Long id;

  @OneToMany(mappedBy = "memeber")
  private List<Order> orders = new ArrayList<>();

  private String name;
  private String city;
  private String street;
  private String zipcode;

}
