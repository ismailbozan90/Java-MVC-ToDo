package org.scamlet.mvc.todo.Controller;

import org.scamlet.mvc.todo.Entity.User;
import org.scamlet.mvc.todo.Service.EmailService;
import org.scamlet.mvc.todo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public EmailController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        User tempUser = new User();
        tempUser.setUsername("Guest");

        model.addAttribute("pageTitle", "Forgot Password");
        model.addAttribute("page", "forgot-password");
        model.addAttribute("user", tempUser);

        return "/email/forgot-password";
    }

    @PostMapping("/forgotPassword")
    public String showForgotPassword(@RequestParam("email") String mail, Model model, RedirectAttributes redirectAttributes){
        User user = userService.findByEmail(mail);
        model.addAttribute("pageTitle", "Forgot Password");
        model.addAttribute("page", "forgot-password");
        if (user == null) {
            User tempUser = new User();
            tempUser.setUsername("Guest");
            model.addAttribute("user", tempUser);
            model.addAttribute("error", "Invalid email");
            return "/email/forgot-password";
        }

        emailService.sendEmail(user.getEmail(), "Mail Test Title", "Mail Test Content");

        model.addAttribute("user", user);
        redirectAttributes.addFlashAttribute("success", "Mail sent");
        return "redirect:/forgot-password";
    }



}
