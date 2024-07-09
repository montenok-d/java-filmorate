package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review create(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());
        return reviewStorage.create(review);
    }

    private void checkUserExists(Long userId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User № %d not found", userId)));
    }

    private void checkFilmExists(Long filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Film № %d not found", filmId)));
    }

    public Review update(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());
        return reviewStorage.update(review);
    }

    public Review findById(Long reviewId) {
        return reviewStorage.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Review № %d not found", reviewId)));
    }

    public List<Review> getAll(Optional<Long> filmId, Long count) {
        if (filmId.isPresent()) {
            checkFilmExists(filmId.get());
            return reviewStorage.getAllByFilmId(filmId.get(), count);
        } else {
            return reviewStorage.getAll(count);
        }
    }

    public void delete(Long id) {
        findById(id);
        reviewStorage.delete(id);
    }

    public void addLike(Long reviewId, Long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewStorage.addLike(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewStorage.addLike(reviewId, userId, false);
    }

    public void deleteLike(Long reviewId, Long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewStorage.deleteLike(reviewId, userId);
    }
}