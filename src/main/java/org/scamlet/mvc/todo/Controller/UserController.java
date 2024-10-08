package org.scamlet.mvc.todo.Controller;

import jakarta.validation.Valid;
import org.scamlet.mvc.todo.Entity.Role;
import org.scamlet.mvc.todo.Entity.User;
import org.scamlet.mvc.todo.Service.RoleService;
import org.scamlet.mvc.todo.Service.UserService;
import org.scamlet.mvc.todo.Util.Session;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Login");
        model.addAttribute("page", "login");
        return "/auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Register");
        model.addAttribute("page", "register");
        model.addAttribute("user", new User());
        return "/auth/register";
    }
    
    @PostMapping("/registerUser")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                               BindingResult bindingResult, 
                               @RequestParam("password_repeat") String password_repeat, Model model, 
                               RedirectAttributes redirectAttributes) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Register");
        model.addAttribute("page", "register");
        model.addAttribute("user", new User());
        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username is already taken");
            return "/auth/register";
        } else if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email is already in use");
            return "/auth/register";
        } else if (!password_repeat.equals(user.getPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "/auth/register";
        } else if (password_repeat.length() < 4 || password_repeat.length() > 24) {
            model.addAttribute("error", "Password must be between 4 and 24 characters");
            return "/auth/register";
        } else if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors());
            return "/auth/register";
        }

        Role role = roleService.findByRole("USER");
        if (role == null) {
            model.addAttribute("error", "Role not found");
            return "/auth/register";
        }
        userService.registerUser(user, role);
        redirectAttributes.addFlashAttribute("success", "User created successfully");
        return "redirect:/register";
    }

}
