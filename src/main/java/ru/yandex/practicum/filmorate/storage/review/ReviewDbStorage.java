package ru.yandex.practicum.filmorate.storage.review;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbc;
    private final ReviewRowMapper mapper;
    private static final String ADD_FEED = "INSERT INTO feed (entity_id, timestamp, user_id, event_type, operation) VALUES (?, ?, ?, ?, ?)";

    @Override
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
        long id = keyHolder.getKey().longValue();
        jdbc.update(ADD_FEED,  review.getReviewId(), Instant.now().toEpochMilli(), review.getUserId(), "REVIEW", "ADD");
        return findById(id).get();
    }

    @Override
    public Review update(Review review) {
        String query = "UPDATE reviews SET content = ?, is_positive = ?  WHERE id = ?";
        jdbc.update(query, review.getContent(), review.getIsPositive(), review.getReviewId());
        Optional<Review> reviewOptional = findById(review.getReviewId());
        reviewOptional.ifPresent(value -> jdbc.update(ADD_FEED, review.getReviewId(), Instant.now().toEpochMilli(), value.getUserId(), "REVIEW", "UPDATE"));
        return findById(review.getReviewId()).get();
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        String query = "SELECT * FROM reviews WHERE id = ?";
        try {
            Review result = jdbc.queryForObject(query, mapper, reviewId);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getAll(Long count) {
        String query = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbc.query(query, mapper, count);
    }

    @Override
    public List<Review> getAllByFilmId(Long filmId, Long count) {
        log.debug("filmId, count {} {}", filmId, count);
        String query = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbc.query(query, mapper, filmId, count);
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM reviews WHERE id = ?";
        Optional<Review> reviewOptional = findById(id);
        reviewOptional.ifPresent(review -> jdbc.update(ADD_FEED, id, Instant.now().toEpochMilli(), review.getUserId(), "REVIEW", "REMOVE"));
        jdbc.update(query, id);
    }

    @Override
    public void addLike(Long reviewId, Long userId, boolean isLike) {
        String query = "MERGE into reviews_likes(user_id, review_id, is_like) VALUES (?, ?, ?)";
        jdbc.update(query, userId, reviewId, isLike);
        updateUseful(reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        String query = "DELETE FROM reviews_likes WHERE user_id = ? AND review_id = ?";
        jdbc.update(query, userId, reviewId);
        updateUseful(reviewId);
    }

    private void updateUseful(Long reviewId) {
        int useful = getUseful(reviewId);
        String sqlQuery = "UPDATE reviews SET useful = ? WHERE id = ?";
        jdbc.update(sqlQuery, useful, reviewId);
    }

    private int getUseful(Long reviewId) {
        String sqlQuery = "SELECT SUM(CASE WHEN is_like = TRUE THEN 1 ELSE -1 END) useful FROM reviews_likes WHERE review_id = ?";
        Integer useful = jdbc.queryForObject(sqlQuery, Integer.class, reviewId);
        if (useful != null) {
            return useful;
        } else {
            return 0;
        }
    }
}
