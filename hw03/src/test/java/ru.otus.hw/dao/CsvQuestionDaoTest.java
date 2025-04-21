package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.exceptions.QuestionReadException;
import java.io.IOException;
import java.util.List;

import ru.otus.hw.domain.Question;
import ru.otus.hw.dao.CsvQuestionDao;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider fileNameProvider;

    @InjectMocks
    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll_SuccessfulLoadingFromResource() throws IOException {
        String testFileName = "test_questions.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(testFileName);

        List<Question> questions = csvQuestionDao.findAll();

        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        assertEquals(1, questions.size());
    }

    @Test
    void testFindAll_ResourceNotFound() {
        // Arrange
        String nonExistentFile = "non_existent_file.csv";
        when(fileNameProvider.getTestFileName()).thenReturn(nonExistentFile);

        // Act & Assert
        QuestionReadException exception = assertThrows(QuestionReadException.class, () -> csvQuestionDao.findAll());
        assertEquals("File not found: " + nonExistentFile, exception.getMessage());
    }
}