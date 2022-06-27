package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/** Repository로 스프링 빈에 등록해줌
 * JpashopApplication의 @SpringBootApplication 어노테이션을 통해 해당 클래스(JpashopApplication)보다 하위에 있어
 * 컴포넌트 스캔 대상이 되어 결과적으로 Repository로 스프링 빈에 등록된다.
 */
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    /**
    @PersistenceContext // 스프링으로부터 em을 주입받는다.
    private EntityManager em;
    **/

    // 스프링 부트, 정확힌 스프링 데이터 JPA에서 지원해줘서 @PersistenceContext대신 @AutoWired 형식으로도 em도 주입 가능하다.
    // 따라서 MemberService에서 한 것처럼 롬복 사용도 가능
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll(){
        // jpql과 sql은 약간의 차이가 있음, sql은 테이블을 대상으로 쿼리한다면 jpql은 엔티티를 대상으로 쿼리한다.
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.username = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
