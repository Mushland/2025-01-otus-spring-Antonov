package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CsvQuestionDao.class)
class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider appProperties;

    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @Test
    @DisplayName("Questions file is incorrect: Test passed")
    public void exceptionThrowFindAll() {
        Mockito.when(appProperties.getTestFileName()).thenReturn("qestion.csv");

        assertThrows(QuestionReadException.class,
                () -> csvQuestionDao.findAll());
    }

    @Test
    @DisplayName("Questions file is correct: Test passed")
    public void successFindAll() {
        List<Question> expectedQuestions = List.of(
                new Question("What is the genre of the game Minecraft?",
                        List.of(new Answer("First-person shooter", false),
                                new Answer("Sandbox with survival elements", true),
                                new Answer("Racing", false))),
                new Question("What is the main type of weapon in the Counter-Strike series?",
                        List.of(new Answer("Swords", false),
                                new Answer("Firearms", true),
                                new Answer("Magic spells", false))));

        Mockito.when(appProperties.getTestFileName()).thenReturn("test_questions.csv");

        List<Question> actualQuestions = csvQuestionDao.findAll();

        assertEquals(expectedQuestions, actualQuestions);
    }
}