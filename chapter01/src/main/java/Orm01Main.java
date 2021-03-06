import domain.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Orm01Main {

  public static void main(String[] args) {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("basic");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      // INSERT
      Member member = new Member();
//      member.setId(1L);
      member.setName("member01");
      em.persist(member);

      // SELECT
//      domain.Member findMember = em.find(domain.Member.class, 1L);
//      domain.Member findMember = em.createQuery("select m from domain.Member m", domain.Member.class).getSingleResult();
//      System.out.println("findMember.getName() = " + findMember.getName());

      // UPDATE
//      findMember.setName("changeMember01");

      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}
