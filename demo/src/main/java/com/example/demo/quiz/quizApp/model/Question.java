package com.example.demo.quiz.quizApp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "question")
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @Column(length = 1000)
    private String questionText;

    @ElementCollection
    private List<String> answers;

    private int correctIndex;

    public Question() {}

    public Question(String subject, String questionText, List<String> answers, int correctIndex) {
        this.subject = subject;
        this.questionText = questionText;
        this.answers = answers;
        this.correctIndex = correctIndex;
    }

}
