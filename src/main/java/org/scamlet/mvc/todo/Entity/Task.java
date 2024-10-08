package org.scamlet.mvc.todo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name="tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 4, max = 24, message = "Title must be between 4-24 characters.")
    @Column(name="title")
    private String title;

    @NotBlank(message = "Content is required")
    @Column(name="content", columnDefinition = "TEXT")
    private String content;

    @Column(name="date")
    private Date date;

    @Column(name="status")
    private int status;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="owner_id")
    private User user;
}
