
package com.example.evaluationvolontaireservice.clients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userAuth", url = "http://localhost:8081")
public interface UserAuthClient {
    


    @GetMapping("/api/userAuth/users/username")
    Long getUserIdByUsername(@RequestHeader("Authorization") String token);

} 