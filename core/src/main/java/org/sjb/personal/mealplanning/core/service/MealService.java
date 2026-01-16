package org.sjb.personal.mealplanning.core.service;

import org.sjb.personal.mealplanning.core.domain.Meal;
import org.sjb.personal.mealplanning.core.repository.MealRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<Meal> findAll() {
        return mealRepository.findAll();
    }

    public Optional<Meal> findById(Long id) {
        return mealRepository.findById(id);
    }

    public Meal save(Meal meal) {
        return mealRepository.save(meal);
    }

    public void deleteById(Long id) {
        mealRepository.deleteById(id);
    }
}