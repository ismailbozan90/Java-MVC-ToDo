package org.scamlet.mvc.todo.Repository;

import org.scamlet.mvc.todo.Entity.Task;
import org.scamlet.mvc.todo.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUser(User user, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :name, '%')) AND t.user = :user")
    Page<Task> searchByNameAndUser(@Param("name") String name, @Param("user") User user, Pageable pageable);

    Page<Task> findByUserAndStatus(User user, int status, Pageable pageable);



}
