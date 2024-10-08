package com.example.demo.DBModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String Description;

    @Column(nullable = false)
    private String Location;

    @Column(nullable = false)
    private LocalDateTime Date;

    @ManyToOne
    @JoinColumn(name = "created_by",nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "cateegory_id")
    private Category category;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
