package org.sjb.personal.mealplanning.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Meal meal;

    private DayOfWeek dayOfWeek;
    private int portionsConsumed;
    private int leftoversCreated;
}