package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller
public class AuthController {
    @GetMapping("/registration")
    public String registrationPage() {
        return "registration";
    }
}
