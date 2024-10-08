package org.scamlet.mvc.todo.Service;

import jakarta.transaction.Transactional;
import org.scamlet.mvc.todo.Entity.Task;
import org.scamlet.mvc.todo.Entity.User;
import org.scamlet.mvc.todo.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public Page<Task> findByUser(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUser(user, pageable);
    }

    public Page<Task> searchPostByUser(int page, int size, User user, String search) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.searchByNameAndUser(search, user, pageable);
    }

    public Page<Task> findByUserAndStatus(int page, int size, User user, int status) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUserAndStatus(user, status, pageable);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public void addTask(Task task) {
        task.setDate(new Date());
        task.setStatus(0);
        taskRepository.save(task);
    }

    @Transactional
    public void updateStatus(Task task) {
        task.setStatus(task.getStatus() == 1 ? 0 : 1);
        taskRepository.save(task);
    }

    @Transactional
    public void updateTask(Task task) {
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

}
