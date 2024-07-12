package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    Optional<Review> findById(Long reviewId);

    List<Review> getAll(Long count);

    List<Review> getAllByFilmId(Long filmId, Long count);

    void delete(Long id);

    void addLike(Long reviewId, Long userId, boolean isLike);

    void deleteLike(Long reviewId, Long userId);
}