package org.sjb.personal.mealplanning.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.DayOfWeek;

@Schema(description = "Details of a meal planned for a specific day")
public record DailyMealPlanDTO(
    @Schema(description = "Day of the week")
    DayOfWeek dayOfWeek,
    @Schema(description = "The meal planned for this day, or null if no meal is needed or leftovers are used")
    MealDTO meal,
    @Schema(description = "Indicates if this meal is leftovers from a previous day")
    boolean isLeftovers,
    @Schema(description = "Name of the meal (useful if it's leftovers)")
    String mealName
) {}