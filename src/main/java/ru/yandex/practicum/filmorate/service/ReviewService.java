package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    public Review create(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());
        return reviewDbStorage.create(review);
    }

    private void checkUserExists(long userId) {
        userDbStorage.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User № %d not found", userId)));
    }

    private void checkFilmExists(long filmId) {
        filmDbStorage.findFilmById(filmId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Film № %d not found", filmId)));
    }

    public Review update(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());
        return reviewDbStorage.update(review);
    }

    public Review findById(long reviewId) {
        return reviewDbStorage.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Review № %d not found", reviewId)));
    }

    public List<Review> getAll(Optional<Long> filmId, int count) {
        if (filmId.isPresent()) {
            checkFilmExists(filmId.get());
            return reviewDbStorage.getAllByFilmId(filmId.get(), count);
        } else {
            return reviewDbStorage.getAll(count);
        }
    }

    public void delete(long id) {
        findById(id);
        reviewDbStorage.delete(id);
    }

    public void addLike(long reviewId, long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewDbStorage.addLike(reviewId, userId, true);
    }

    public void addDislike(long reviewId, long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewDbStorage.addLike(reviewId, userId, false);
    }

    public void deleteLike(long reviewId, long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewDbStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        checkUserExists(userId);
        findById(reviewId);
        reviewDbStorage.deleteLike(reviewId, userId);
    }
}
