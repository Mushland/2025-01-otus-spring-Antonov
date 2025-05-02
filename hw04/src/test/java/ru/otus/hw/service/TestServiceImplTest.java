package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.dao.QuestionDao;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestServiceImpl.class)
public class TestServiceImplTest {

    @MockBean
    private QuestionDao questionDao;

    @MockBean
    private LocalizedIOService ioService;

    @Autowired
    private TestServiceImpl testService;

    @Test
    @DisplayName("Should handle correct answer correctly")
    void shouldHandleCorrectAnswer() {
        // Arrange
        Student student = new Student("John", "Doe");
        Question question = new Question("What is 2 + 2?",
                List.of(new Answer("3", false), new Answer("4", true)));

        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readIntForRangeWithPromptLocalized(
                1, 2, "TestService.choose.answers", "TestService.wrong.input"
        )).thenReturn(2);

        // Act
        TestResult result = testService.executeTestFor(student);

        // Assert
        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getRightAnswersCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle wrong answer correctly")
    void shouldHandleWrongAnswer() {
        // Arrange
        Student student = new Student("Jane", "Smith");
        Question question = new Question("What is 2 + 2?",
                List.of(new Answer("3", false), new Answer("4", true)));

        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readIntForRangeWithPromptLocalized(
                1, 2, "TestService.choose.answers", "TestService.wrong.input"
        )).thenReturn(1);

        // Act
        TestResult result = testService.executeTestFor(student);

        // Assert
        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getRightAnswersCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle multiple questions correctly")
    void shouldHandleMultipleQuestions() {
        // Arrange
        Student student = new Student("Alice", "Johnson");

        Question q1 = new Question("What is 2 + 2?",
                List.of(new Answer("3", false), new Answer("4", true)));
        Question q2 = new Question("What is the capital of France?",
                List.of(new Answer("Paris", true), new Answer("Rome", false)));

        when(questionDao.findAll()).thenReturn(List.of(q1, q2));
        when(ioService.readIntForRangeWithPromptLocalized(
                anyInt(), anyInt(), eq("TestService.choose.answers"), eq("TestService.wrong.input")
        )).thenReturn(2).thenReturn(1);

        // Act
        TestResult result = testService.executeTestFor(student);

        // Assert
        assertThat(result.getStudent()).isEqualTo(student);
        assertThat(result.getAnsweredQuestions()).hasSize(2);
        assertThat(result.getRightAnswersCount()).isEqualTo(2);
    }
}