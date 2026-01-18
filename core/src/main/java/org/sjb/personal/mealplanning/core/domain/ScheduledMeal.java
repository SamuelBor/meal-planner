package org.sjb.personal.mealplanning.core.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;

@Entity
@Data
@Builder
public class ScheduledMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Meal meal;

    private DayOfWeek dayOfWeek;
    private int portionsConsumed;
    private int leftoversCreated;

    public ScheduledMeal() {
    }

    public ScheduledMeal(Long id, Meal meal, DayOfWeek dayOfWeek, int portionsConsumed, int leftoversCreated) {
        this.id = id;
        this.meal = meal;
        this.dayOfWeek = dayOfWeek;
        this.portionsConsumed = portionsConsumed;
        this.leftoversCreated = leftoversCreated;
    }
}