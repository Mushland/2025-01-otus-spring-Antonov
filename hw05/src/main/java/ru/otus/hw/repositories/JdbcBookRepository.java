package ru.otus.hw.repositories;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public JdbcBookRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        Book book;
        try {
            book = namedParameterJdbcOperations
                    .queryForObject("SELECT books.id, books.title, books.author_id, books.genre_id," +
                                    " authors.full_name, genres.name  FROM BOOKS " +
                                    " left join authors on authors.id = books.author_id " +
                                    " left join genres on genres.id = books.genre_id where books.id = :id",
                            params,
                            new BookRowMapper());
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        return namedParameterJdbcOperations
                .query("SELECT books.id, books.title, books.author_id, books.genre_id," +
                        " authors.full_name, genres.name  FROM BOOKS " +
                        "left join authors on authors.id = books.author_id\n" +
                        "left join genres on genres.id = books.genre_id ", new BookRowMapper());
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        Map<String, Object> param = Collections.singletonMap("id", id);
        namedParameterJdbcOperations.update("delete from books where id = :id", param);
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        namedParameterJdbcOperations.update("insert into books (title, author_id, genre_id) " +
                "values (:title, :author_id, :genre_id)", params, keyHolder);
        if (keyHolder.getKeyAs(Long.class) != null) {
            book.setId(keyHolder.getKeyAs(Long.class));
        }
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", book.getId());
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        int rowsChanged = namedParameterJdbcOperations.update("update books set title = :title," +
                " author_id = :author_id, genre_id = :genre_id  where id = :id", params);
        if (rowsChanged == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " is not found");
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("books.id");
            String title = rs.getString("books.title");
            long authorId = rs.getLong("books.author_id");
            String authorName = rs.getString("authors.full_name");
            long genreId = rs.getLong("books.genre_id");
            String genreName = rs.getString("genres.name");
            return new Book(id, title, new Author(authorId, authorName), new Genre(genreId, genreName));
        }
    }
}