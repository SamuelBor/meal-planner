package org.sjb.personal.mealplanning.core.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
public class WeeklyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate weekStartDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduledMeal> scheduledMeals = new ArrayList<>();

    public WeeklyPlan() {
    }

    public WeeklyPlan(Long id, LocalDate weekStartDate, List<ScheduledMeal> scheduledMeals) {
        this.id = id;
        this.weekStartDate = weekStartDate;
        this.scheduledMeals = scheduledMeals;
    }
}