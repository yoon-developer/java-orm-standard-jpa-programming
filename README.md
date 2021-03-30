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

# 3. 엔티티 맵핑
## 3.1. 객체와 테이블 매핑
> @Entity
- JPA 관리
- JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 필수

```java
 @Entity
 public class Member {
 
 }
```

```text
=== 주의 ===
- 기본 생성자 필수(파라미터가 없는 public 또는 protected 생성자)
- final 클래스, enum, interface, inner 클래스 사용X
- 저장할 필드에 final 사용 X
```

> @Table
- 엔티티와 매핑할 테이블 지정

| 속성                   | 기능                    | 기본값        | 
|------------------------|-----------------------|------------| 
| name                   | 매핑할 테이블 이름            | 엔티티 이름을 사용 | 
| catalog                | 데이터베이스 catalog 매핑     |            | 
| schema                 | 데이터베이스 schema 매핑      |            | 
| uniqueConstraints(DDL) | DDL 생성 시에 유니크 제약 조건 생 |            | 


```java
@Entity
@Table(name = "USER")
public class Member {

  @Id
  private Long id;
  private String name;
}
```

## 3.2. 데이터베이스 스키마 자동 생성
- DDL을 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- 데이터베이스 방언을 활용해서 데이터베이스에 맞는 적절한 DDL 생성

```xml
- persistence.xml

<persistence-unit name="basic">
  <properties>
    <property name="hibernate.hbm2ddl.auto" value="create" />
  </properties>
</persistence-unit>
```


| 옵션          | 설명                                | 
|-------------|-----------------------------------| 
| create      | 기존테이블 삭제 후 다시 생성 (DROP + CREATE)  | 
| create-drop | create와 같으나 종료시점에 테이블 DROP        | 
| update      | 변경분만 반영(운영DB에는 사용하면 안됨)           | 
| validate    | 엔티티와 테이블이 정상 매핑되었는지만 확인           | 
| none        | 사용하지 않음                           | 

```text
=== 주의 ===
- 운영 장비에는 절대 create, create-drop, update 사용하면 안된다. 
- 개발 초기 단계는 create 또는 update 
- 테스트 서버는 update 또는 validate 
- 스테이징과 운영 서버는 validate 또는 none
```

> 제약조건 추가
```java
@Column(nullable = false, length = 10) 
```

> 유니크 제약조건 추가
```java
@Table(uniqueConstraints = {@UniqueConstraint( name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"} )}) 
```

## 3.3. 필드와 컬럼 맵핑
> Annotation

| 어노테이션       | 설명                       | 
|-------------|--------------------------| 
| @Column     | 컬럼 매핑                    | 
| @Enumerated | enum 타입 매핑               | 
| @Lob        | BLOB, CLOB 매핑           | 
| @Transient  | 정 필드를 컬럼에 매핑하지 않음(매핑 무시) | 

> @Column

| 속성                     | 설명                                                                                                                                                         | 기본값                   | 
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------| 
| name                   | 필드와 매핑할 테이블의 컬럼 이름 & 객체의 필드 이름                                                                                                                             |                       | 
| insertable & updatable | 등록 변경 가능 여부                                                                                                                                                | TRUE                  | 
| nullable(DDL)          | null 값의 허용 여부를 설정한다. false로 설정하면 DDL 생성 시에 not null 제약조건이 붙는다.                                                                                             |                       | 
| unique(DDL)            |  @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 걸 때 사용한다.                                                                                              |                       | 
| columnDefinition(DDL)  | 데이터베이스 컬럼 정보를 직접 줄 수 있다. ex) varchar(100) default ‘EMPTY'                                                                                                  |                       | 
| length(DDL)            | 문자 길이 제약조건. String 타입에만 사용한다.                                                                                                                              | 255                   | 
| precision scale(DDL)   | BigDecimal 타입에서 사용한다. (BigInteger도 사용할 수 있다). precision은 소수점을 포함한 전체 자 릿수를 scale은 소수의 자릿수다. 참고로 double float 타입에는 적용되지 않는다. 아주 큰 숫자나 정밀한 소수를 다루어야 할 때만 사용한다. |  precision=19 scale=2 | 

> @Enumerated

| 속성    | 설명                                                                           | 기본값              | 
|-------|------------------------------------------------------------------------------|------------------| 
| value |  EnumType.ORDINAL: enum 순서를 데이터베이스에 저장. EnumType.STRING: enum 이름을 데이터베이스에 저장 | EnumType.ORDINAL | 

```text
=== 주의 ===
ORDINAL 사용X
```

> @Lob
- 데이터베이스 BLOB, CLOB 타입과 매핑
- 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
  - CLOB: String, char[], java.sql.CLOB
  - BLOB: byte[], java.sql. BLOB

> @Transient
- 필드 매핑X 
- 데이터베이스에 저장X, 조회X 
- 주로 메모리상에서만 임시로 어떤 값을 보관하고 싶을 때 사용

## 3.4. 기본 키 매핑
- 직접 할당: @Id만 사용
- 자동 생성(@GeneratedValue) 
  - IDENTITY: 데이터베이스에 위임, MYSQL 
  - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE 
    - @SequenceGenerator 필요
  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
    - @TableGenerator 필요
  - AUTO: 방언에 따라 자동 지정, 기본
  
> IDENTITY

- 기본 키 생성을 데이터베이스에 위임
- 주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용 (예: MySQL의 AUTO_ INCREMENT) 
- AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 실행한 이후에 ID 값을 알 수 있음
- IDENTITY 전략은 em.persist() 시점에 즉시 INSERT SQL 실행 하고 DB에서 식별자를 조회

```java
@Entity 
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
```

> SEQUENCE
- 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스) 
- 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용

| 속성               | 설명                                                                                    | 기본값                | 
|------------------|---------------------------------------------------------------------------------------|--------------------| 
| name             | 식별자 생성기 이름                                                                            | 필수                 | 
| sequenceName     | 데이터베이스에 등록되어 있는 시퀀스 이름                                                                | hibernate_sequence | 
| initialValue     | DDL 생성 시에만 사용됨. 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정한다.                                     | 1                  | 
| allocationSize   | 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨 데이터베이스 시퀀스 값이 하나씩 증가하도록 설정되어 있으면 이 값 을 반드시 1로 설정해야 한다. | 50                 | 
| catalog, schema |  데이터베이스 catalog, schema 이름                                                           |                    | 

```java
@Entity 
@SequenceGenerator( 
 name = "MEMBER_SEQ_GENERATOR", 
 sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
 initialValue = 1, allocationSize = 1) 
public class Member { 
  @Id 
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR") 
  private Long id; 
```

> Table

- 키 생성 전용 테이블을 하나 만들어서 데이터베이스 시퀀스를 흉내내는 전략
- 장/단점
  - 장점: 모든 데이터베이스에 적용 가능
  - 단점: 성능

```mysql
create table MY_SEQUENCES ( 
 sequence_name varchar(255) not null, 
 next_val bigint, 
 primary key ( sequence_name ) 
)
```

```java
Entity 
@TableGenerator( 
 name = "MEMBER_SEQ_GENERATOR", 
 table = "MY_SEQUENCES", 
 pkColumnValue = “MEMBER_SEQ", allocationSize = 1) 
public class Member { 
  @Id 
  @GeneratedValue(strategy = GenerationType.TABLE, 
  generator = "MEMBER_SEQ_GENERATOR") 
  private Long id; 
```

| 속성                     | 설명                              | 기본값                 | 
|------------------------|---------------------------------|---------------------| 
| name                   | 식별자 생성기 이름                      | 필수                  | 
| table                  | 키생성 테이블명                        | hibernate_sequences | 
| pkColumnName           | 시퀀스 컬럼명                         | sequence_name       | 
| valueColumnName        | 시퀀스 값 컬럼명                       | next_val            | 
| pkColumnValue          | 키로 사용할 값 이름                     | 엔티티 이름              | 
| initialValue           | 초기 값. 마지막으로 생성된 값이 기준이다.        | 0                   | 
| allocationSize         | 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨) | 50                  | 
| catalog, schema       | 데이터베이스 catalog, schema 이름      |                     | 
| uniqueConstraints(DDL) | 유니크 제약 조건을 지정할 수 있다.            |                     | 


## 3.5. 데이터 중심 설계의 문제점
- 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식
- 테이블의 외래키를 객체에 그대로 가져옴
- 객체 그래프 탐색이 불가능
- 참조가 없으므로 UML도 잘못됨
