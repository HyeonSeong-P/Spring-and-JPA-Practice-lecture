package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//JPA의 모든 데이터 변경은 가급적이면 트랜잭션 안에서 수행되어야 한다. 쓸 수 있는 옵션이 많은 spring의 Transactional 어노테이션을 사용하자.
@Transactional(readOnly = true) // 읽기의 경우 readOnly를 true로 설정. DB의 자원 낭비를 줄일 수 있다.
@RequiredArgsConstructor // lombok의 어노테이션, AllArgsConstructor와 달리 final이 붙은 필드에 대한 생성자를 자동으로 만들어준다.
public class MemberService {

    private final MemberRepository memberRepository; // 주입받을 필드는 final로 설정하자. 컴파일 타임에 오류 잡기가 쉽다.

    /*
    @Autowired // 스프링은 생성자가 하나만 있을 경우엔 이 어노테이션을 안붙여도 자동으로 injection 해준다. 이같은 이유로 @RequiredArgsConstructor만으로 정상 동작한다.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    /**
     * 회원 가입
     */
    @Transactional // 이렇게 메서드에 따로 설정하면 따로 설정한 것이 우선권을 갖는다(readOnly의 디폴트가 false이므로 readOnly=false 이다)
    public Long join(Member member){
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId(); // 커맨더랑 쿼리를 분리하라는 법칙에 의해 리턴값을 거의 안만들고 id만을 리턴한다.(id로도 조회가 가능하니까)
    }

    private void validateDuplicateMember(Member member) {
        // 실무에선 멀티 스레드 환경을 고려해서 member의 name을 유니크 제약 조건을 걸어주는 것을 권장.(ex.동시에 A라는 이름의 멤버가 join을 요청할 때)
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */

    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 특정 회원 조회
     */
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}
