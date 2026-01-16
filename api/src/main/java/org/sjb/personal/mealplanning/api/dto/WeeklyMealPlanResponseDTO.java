package org.sjb.personal.mealplanning.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(description = "Response object containing the weekly meal plan and aggregated ingredients")
public record WeeklyMealPlanResponseDTO(
    @Schema(description = "List of daily meal plans")
    List<DailyMealPlanDTO> dailyPlans,
    @Schema(description = "Aggregated list of ingredients needed for the week")
    Map<String, Double> aggregatedIngredients
) {}