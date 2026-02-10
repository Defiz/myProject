package vlad.pr.projectCRUD.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping
    public String listUsers() {
        return "admin-list";
    }

}
