package jpabook.jpashop;


import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ExtendWith(SpringExtension.class) // junit5에서 junit4의 RunWith가 동일한 동작
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional // 이 어노테이션이 테스트에 들어있으면 데이터 생성하고 한걸 롤백함
    @Rollback(false)
    public void testMember() throws Exception{
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        // JPA 엔티티 동일성 보장, 같은 트랜잭션 안에서 저장하고 조회하면 영속성 context가 똑같으니 같은 영속성 context 안에선 id값이 같으면 같다고 여겨지니 true가 나와야 함.
        Assertions.assertThat(findMember).isEqualTo(member);

    }
}