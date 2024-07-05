package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody @Valid Review review) {
        log.info("POST / reviews / {}", review);
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        log.info("PUT / reviews / {}", review.getReviewId());
        return reviewService.update(review);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable("id") long id) {
        log.info("GET /reviews/{}", id);
        return reviewService.findById(id);
    }

    @GetMapping
    public List<Review> getAll(@RequestParam Optional<Long> filmId,
                               @RequestParam(defaultValue = "10") @Positive int count) {
        log.info("GET /reviews/");
        return reviewService.getAll(filmId, count);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        reviewService.delete(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") long reviewId,
                        @PathVariable("userId") long userId) {
        log.info("PUT reviews/{}/like/{}", reviewId, userId);
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") long reviewId,
                           @PathVariable("userId") long userId) {
        log.info("PUT reviews/{}/dislike/{}", reviewId, userId);
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long reviewId,
                        @PathVariable("userId") long userId) {
        log.info("DELETE reviews/{}/like/{}", reviewId, userId);
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") long reviewId,
                           @PathVariable("userId") long userId) {
        log.info("DELETE reviews/{}/dislike/{}", reviewId, userId);
        reviewService.deleteDislike(reviewId, userId);
    }
}
