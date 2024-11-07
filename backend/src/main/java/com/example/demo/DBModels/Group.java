package com.example.demo.DBModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "user_groups") // Modificat din "groups" in "user_groups"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    private String name;

    // Un grup contine mai multi utilizatori
    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"), // corectat sa fie referinta corecta la "group_id"
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members;
}
