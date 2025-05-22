package com.example.demo.quiz.quizApp.service;

import com.example.demo.quiz.quizApp.model.Question;
import com.example.demo.quiz.quizApp.repository.QuestionRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfProcessingService {

    private final QuestionRepository repository;

    public PdfProcessingService(QuestionRepository repository) {
        this.repository = repository;
    }

    public List<Question> processPdf(MultipartFile file, String subject, int questionCount, int startQuestion, String endQuestion) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();

        String[] lines = text.split("\\r?\\n");
        List<Question> questions = new ArrayList<>();

        int currentQuestionNumber = 0;
        int endQ;

        if ("last".equalsIgnoreCase(endQuestion)) {
            endQ = Integer.MAX_VALUE;
        } else {
            try {
                endQ = Integer.parseInt(endQuestion);
            } catch (NumberFormatException e) {
                endQ = Integer.MAX_VALUE;
            }
        }

        String questionText = null;
        List<String> answers = new ArrayList<>();
        int correctIndex = -1;
        int answerIndex = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("•") || line.startsWith("√")) {
                if (questionText == null) {
                    // Yeni sual
                    questionText = line.substring(1).trim();
                    answers.clear();
                    correctIndex = -1;
                    answerIndex = 0;
                } else {
                    // Cavablar
                    if (line.startsWith("√")) {
                        answers.add(line.substring(1).trim());
                        correctIndex = answerIndex;
                    } else {
                        answers.add(line.substring(1).trim());
                    }
                    answerIndex++;
                }
            } else if (!line.isEmpty() && questionText != null && !answers.isEmpty()) {
                // Sual bitdi, əlavə et
                currentQuestionNumber++;
                if (currentQuestionNumber >= startQuestion && currentQuestionNumber <= endQ && questions.size() < questionCount) {
                    questions.add(new Question(subject, questionText, new ArrayList<>(answers), correctIndex));
                }
                questionText = null;
                answers.clear();
                correctIndex = -1;
                answerIndex = 0;

                if (line.startsWith("•") || line.startsWith("√")) {
                    questionText = line.substring(1).trim();
                }
            }
        }

        // Sonuncu sualı əlavə et
        if (questionText != null && !answers.isEmpty() && questions.size() < questionCount) {
            currentQuestionNumber++;
            if (currentQuestionNumber >= startQuestion && currentQuestionNumber <= endQ) {
                questions.add(new Question(subject, questionText, new ArrayList<>(answers), correctIndex));
            }
        }

        repository.saveAll(questions);

        return questions;
    }
}
