package com.example.demo.quiz.quizApp.controller;

import com.example.demo.quiz.quizApp.model.Question;
import com.example.demo.quiz.quizApp.repository.QuestionRepository;
import com.example.demo.quiz.quizApp.service.PdfProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QuizController {

    private final PdfProcessingService pdfService;
    private final QuestionRepository repository;

    public QuizController(PdfProcessingService pdfService, QuestionRepository repository) {
        this.pdfService = pdfService;
        this.repository = repository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("subject") String subject,
            @RequestParam(value = "questionCount", defaultValue = "1") int questionCount,
            @RequestParam(value = "startQuestion", defaultValue = "1") int startQuestion,
            @RequestParam(value = "endQuestion", defaultValue = "last") String endQuestion) {

        try {
            if (questionCount < 1) questionCount = 1;
            if (startQuestion < 1) startQuestion = 1;

            List<Question> questions = pdfService.processPdf(file, subject, questionCount, startQuestion, endQuestion);
            return ResponseEntity.ok(questions);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("PDF faylının emalı zamanı xəta baş verdi: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Serverdə gözlənilməz xəta baş verdi: " + e.getMessage());
        }
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions(@RequestParam("subject") String subject) {
        List<Question> questions = repository.findBySubject(subject);
        if (questions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(questions);
    }
}
