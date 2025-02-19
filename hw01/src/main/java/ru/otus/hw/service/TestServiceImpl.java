package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        List<Question> questions = questionDao.findAll();

        for (Question question : questions) {
            ioService.printFormattedLine("Question: %s%n", question.text());

            int index = 1;
            for (Answer answer : question.answers()) {
                ioService.printFormattedLine("%d. %s%n", index++, answer.text());
            }
            ioService.printFormattedLine("", question.text());
        }

    }
}
