package org.sjb.personal.mealplanning.core.repository;

import org.sjb.personal.mealplanning.core.domain.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
}