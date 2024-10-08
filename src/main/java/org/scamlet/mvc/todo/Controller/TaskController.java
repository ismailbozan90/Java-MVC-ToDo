package org.scamlet.mvc.todo.Controller;

import jakarta.validation.Valid;
import org.scamlet.mvc.todo.Entity.Task;
import org.scamlet.mvc.todo.Entity.User;
import org.scamlet.mvc.todo.Service.TaskService;
import org.scamlet.mvc.todo.Service.UserService;
import org.scamlet.mvc.todo.Util.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.Optional;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Home Page");
        model.addAttribute("page", "home");

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Page<Task> tasks = taskService.findByUser(page, size, user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "index";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam("search") String searchString,
                         @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {

        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        } else if(searchString.isEmpty()) {
            return "redirect:/";
        } else if(searchString.length() < 2 || searchString.length() > 24) {
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Search " + searchString + " Page " + (page+1));
        model.addAttribute("page", "search");

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Page<Task> tasks = taskService.searchPostByUser(page, size, user, searchString);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        model.addAttribute("searchString", searchString);
        return "/task/search";
    }

    @GetMapping("/completed")
    public String completedTasks(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Completed Tasks");
        model.addAttribute("page", "completed-task");

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Page<Task> tasks = taskService.findByUserAndStatus(page, size, user, 1);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "/task/completed";
    }

    @GetMapping("/ongoing")
    public String ongoingTasks(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("pageTitle", "Ongoing Tasks");
        model.addAttribute("page", "ongoing-task");

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Page<Task> tasks = taskService.findByUserAndStatus(page, size, user, 0);
        model.addAttribute("tasks", tasks);
        model.addAttribute("user", user);
        return "/task/ongoing";
    }

    @GetMapping("/task/add-task")
    public String addTaskForm(Model model) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Task task = new Task();
        task.setUser(user);

        model.addAttribute("pageTitle", "Add Task");
        model.addAttribute("page", "add-task");
        model.addAttribute("user", user);
        model.addAttribute("task", task);
        return "/task/add-task";
    }

    @PostMapping("/task/addTask")
    public String addTask(@Valid @ModelAttribute("task") Task task, BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Add Task");
        model.addAttribute("page", "add-task");
        model.addAttribute("user", user);

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors());

            model.addAttribute("task", task);
            return "/task/add-task";
        }

        model.addAttribute("task", task);

        task.setUser(user);
        taskService.addTask(task);

        redirectAttributes.addFlashAttribute("success", "Task created successfully");
        return "redirect:/task/add-task";

    }

    @GetMapping("/task/change-status")
    public String updateStatus(@RequestParam("taskId") Long id) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        Optional<Task> task = taskService.findById(id);
        if (task.isEmpty()) {
            return "redirect:/";
        }

        taskService.updateStatus(task.get());

        return "redirect:/";
    }

    @GetMapping("/task/update-task")
    public String updateTaskForm(@RequestParam("taskId") Long id, Model model) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Task> task = taskService.findById(id);
        if (task.isEmpty()) {
            return "redirect:/";
        }
        task.get().setUser(user);

        model.addAttribute("pageTitle", "Update Task");
        model.addAttribute("page", "update-task");
        model.addAttribute("user", user);
        model.addAttribute("task", task.get());
        return "/task/update-task";
    }

    @PostMapping("/task/updateTask")
    public String updateTask(@Valid @ModelAttribute("task") Task task, BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Update Task");
        model.addAttribute("page", "update-task");
        model.addAttribute("user", user);
        model.addAttribute("task", task);
        if (bindingResult.hasErrors()) {
            model.addAttribute("taskId", task.getId());
            model.addAttribute("error", bindingResult.getAllErrors());
            return "/task/update-task";
        }
        task.setUser(user);
        task.setDate(new Date());
        taskService.updateTask(task);
        redirectAttributes.addFlashAttribute("success", "Task updated successfully");
        return "redirect:/task/update-task?taskId="+task.getId();

    }

    @GetMapping("/task/delete-task")
    public String deleteTask(@RequestParam("taskId") Long id, Model model) {

        Authentication authentication = Session.getAuthentication();
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Task> task = taskService.findById(id);
        if (task.isEmpty()) {
            return "redirect:/";
        }

        taskService.deleteTask(task.get());

        return "redirect:/";
    }

}
