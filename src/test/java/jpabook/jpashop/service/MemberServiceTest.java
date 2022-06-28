package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest // JUnit5에선 @RunWith(SpringRunner.class)가 포함되어있다.
//@ExtendWith(SpringExtension.class) // junit5에서 junit4의 RunWith가 동일한 동작
@Transactional // 이 어노테이션이 테스트 케이스에서 사용되면 테스트 이후 롤백한다.
class MemberServiceTest {
    @Autowired
    MemberService memberService; // 테스트 케이스니까 간단하게 필드 인젝션

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    // 테스팅 결과를 쭉 보면 insert문이 없는데 persist를 한다고 insert 되는 게 아니라
    // 트랜잭션을 commit 해야 최종적으로 영속성 컨텍스트되어 있는 것들이 insert된다.
    // 근데 스프링에선 @Transactional이 테스트 케이스에 있으면 기본적으로 커밋을 안하고 롤백을 해버린다.
    // 그래서 insert문을 눈으로 확인하려면 @Rollback(false) 어노테이션을 추가해줘야 한다.
    @Test
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        
        //when
        Long savedId = memberService.join(member);

        //then
        // em.flush(); // em을 들고와 flush해주면 롤백 어노테이션 없이 바로 반영 가능하다. 그래서 insert문을 볼수 있음.
        // 하지만 롤백은 테스트 이후에 정상 적용되어 insert된 것들도 롤백됨
        Assertions.assertEquals(member,memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        //when
        memberService.join(member1);

        //then
        Assertions.assertThrows(IllegalStateException.class,() -> memberService.join(member2)); //예외가 발생해야 한다!!!
    }
}