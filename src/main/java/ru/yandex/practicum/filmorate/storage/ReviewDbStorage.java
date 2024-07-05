package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage {

    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;

    public Review create(Review review) {
        String query = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?);";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    public Review update(Review review) {
        String query = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? WHERE id = ?";
        jdbc.update(query, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(), review.getUseful(), review.getReviewId());
        return review;
    }

    public Optional<Review> findById(long reviewId) {
        String query = "SELECT * FROM reviews WHERE id = ?";
        try {
            Review result = jdbc.queryForObject(query, mapper, reviewId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    public List<Review> getAll(int count) {
        String query = "SELECT * FROM reviews LIMIT ?";
        return jdbc.query(query, mapper, count);
    }

    public List<Review> getAllByFilmId(long filmId, int count) {
        log.debug("filmId, count {} {}", filmId, count);
        String query = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
        return jdbc.query(query, mapper, filmId, count);
    }

    public void delete(long id) {
        String query = "DELETE FROM reviews WHERE id = ?";
        jdbc.update(query, id);
    }

    public void addLike(long reviewId, long userId, boolean isLike) {
        String query = "INSERT INTO reviews_likes (user_id, review_id, is_like) VALUES (?, ?, ?)";
        jdbc.update(query, reviewId, userId, isLike);
    }

    public void deleteLike(long reviewId, long userId) {
        String query = "DELETE FROM reviews_likes WHERE user_id = ? AND review_id = ?";
        jdbc.update(query, reviewId, userId);
    }

    public void increaseUseful(long reviewId) {
        String query = "UPDATE reviews SET useful = useful + 1 WHERE id = ?";
        jdbc.update(query, reviewId);
    }

    public void decreaseUseful(long reviewId) {
        String query = "UPDATE reviews SET useful = useful - 1 WHERE id = ?";
        jdbc.update(query, reviewId);
    }
}
