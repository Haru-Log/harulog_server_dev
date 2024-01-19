package goojeans.harulog.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * http://localhost:8080/swagger-ui/index.html 로 접속하면 Swagger UI 확인 가능
 */
@RestController
public class FirstController {

    @RequestMapping("/")
    public String test() {
        return "hello";
    }

    @GetMapping("/api/swagger-test")
    @Operation(
            summary = "Swagger 테스트 API",
            description = "Swagger 테스트 API 입니다."
    )
    public ResponseEntity<String> swaggerTest() {
        return ResponseEntity.ok("Swagger 테스트 API 입니다.");
    }
}
