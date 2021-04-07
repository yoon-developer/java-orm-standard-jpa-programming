import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

  @Column(name = "USERNAME")
  private String name;
  private int age;

  @ManyToOne
  @JoinColumn(name = "TEAM_ID")
  private Team team;

  public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }
}
