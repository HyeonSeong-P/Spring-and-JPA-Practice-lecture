package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository // 엔티티들을 찾아주는 애
public class MemberRepository {
    @PersistenceContext // 이 어노테이션을 사용하면 스프링 컨테이너가 EntityManager를 주입해줌(EntityManager는 앞서 설정한 라이브러리들에 의해 자동 생성된다)
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();// 커맨더랑 쿼리를 분리하라는 법칙에 의해 리턴값을 거의 안만들고 id만을 리턴한다.(id로도 조회가 가능하니까)
    }

    public Member find(Long id){
        return em.find(Member.class, id);
    }


}
