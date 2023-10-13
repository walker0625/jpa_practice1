package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpaPractice1Application {

    public static void main(String[] args) {
        SpringApplication.run(JpaPractice1Application.class, args);
    }

    // Entity를 바로 응답하는 경우 필요한 라이브러리 - 실문 사용x(프록시를 jackson화 하는 경우 사용)
    @Bean
    Hibernate5Module hibernate5Module() {
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        // lazy 로딩의 경우도 null이 아니라 조회 값으로 채움
        // hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
        return hibernate5Module;
    }

}
