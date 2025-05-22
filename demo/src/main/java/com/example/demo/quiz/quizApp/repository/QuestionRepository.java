package com.example.demo.quiz.quizApp.repository;

import com.example.demo.quiz.quizApp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubject(String subject);

}
