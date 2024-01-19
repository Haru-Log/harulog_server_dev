package goojeans.harulog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  Swagger 설정
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("HaruLog API")
                        .description("간편한 기록, 갓생살기 SNS HaruLog!")
                        .version("1.0.0"));
        // todo: Spring Security 적용 후, API 문서에 인증 정보 추가
        // https://velog.io/@najiexx/Spring-Boot-3%EC%97%90-Swagger-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0springdoc-openapi
    }
}
