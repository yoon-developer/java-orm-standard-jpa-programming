import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Orm02Main {

  public static void main(String[] args) {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("basic");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    tx.begin();

    try {
      // 비영속
      Member member = new Member();
//      member.setId(1L);
      member.setName("member01");

      // 영속 && 쓰기 지연
      em.persist(member);

      System.out.println("=====");
      // 1차캐시
      Member findMember01 = em.find(Member.class, 1L);
      Member findMember02 = em.find(Member.class, 1L);
      System.out.println("=====");

      // 동일성 보장
      System.out.println("findeMember01 == FindMember02 :" + (findMember01 == findMember02));

      // 변경 감지
      findMember01.setName("changeMember01");

      // 데이터베이스에 SQL Query 실행
      tx.commit();
    } catch (Exception e) {
      tx.rollback();
    } finally {
      em.close();
    }
    emf.close();
  }
}
