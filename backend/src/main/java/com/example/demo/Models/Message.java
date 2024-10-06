package com.example.demo.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    //Un mesaj poate fi trimis intr-un singur grup
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    //Un mesaj poate fi trimis catre un singur user
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(nullable = false)
    private String content;

    private LocalDateTime sentAt;

    private boolean readByAll;

    //metoda pentru a verifica daca este mesaj de grup sau nu
    private boolean isGroupMessage(){
        return group != null;
    }
}
