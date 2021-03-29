Java ORM Standard JPA Programming
==========

# 1. 설명

## 1.1. JPA
- Java Persistence API
- 자바 진영의 ORM 기술 표준

## 1.2. ORM
- Object-relational mapping(객체 관계 매핑)
- 객체는 객체대로 설계
- 관계형 데이터베이스는 관계형 데이터베이스대로 설계
- ORM 프레임워크가 중간에서 매핑
- 대중적인 언어에는 대부분 ORM 기술이 존재

## 1.3. JPA 사용 이유
- SQL 중심적인 개발에서 객체 중심으로 개발
- 생산성
- 유지보수
- 패러다임의 불일치 해결
- 성능
- 데이터 접근 추상화와 벤더 독립성
- 표준

# 2. 영속성 관리
## 2.1. 엔티티의 생명주기

> 비영속(new/transient)

영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
```java
// 객체를 생성한 상태(비영속) 
Member member = new Member(); 
member.setId("member1"); 
member.setUsername("회원1");
```

> 영속 (managed)

영속성 컨텍스트에 관리되는 상태
```java
// 객체를 생성한 상태(비영속) 
Member member = new Member(); 
member.setId("member1"); 
member.setUsername(“회원1”);

EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

// 객체를 저장한 상태(영속)
em.persist(member);
```

> 준영속 (detached)

영속성 컨텍스트에 저장되었다가 분리된 상태
```java
// 회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태 
em.detach(member); 
```

> 삭제 (removed)

엔티티 삭제
```java
// 객체를 삭제한 상태(삭제) 
em.remove(member);
```

## 2.2. 영속성 컨텍스트의 이점
- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연 (transactional write-behind)
- 변경 감지(Dirty Checking)
- 지연 로딩(Lazy Loading)

```text
1차 캐시조회
(YES) -> return
(NO) -> 데이터베이스 -> return
```

## 2.3. 플러시
영속성 컨텍스트의 변경내용을 데이터베이스에 반영
- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터베이스에 전송 (등록, 수정, 삭제 쿼리)

### 2.3.1. 옵션
- FlushModeType.AUTO: 커밋이나 쿼리를 실행할 때 플러시 (기본값)
- FlushModeType.COMMIT: 커밋할 때만 플러시

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("basic");
EntityManager em = emf.createEntityManager();

em.setFlushMode(FlushModeType.COMMIT)
```

## 2.4. 준영속 상태로 만드는 방법
- em.detach(entity): 특정 엔티티만 준영속 상태로 전환
- em.clear(): 영속성 컨텍스트를 완전히 초기화 
- em.close(): 영속성 컨텍스트를 종료

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("basic");
EntityManager em = emf.createEntityManager();

em.detach(entity)
em.clear()
em.close()
```