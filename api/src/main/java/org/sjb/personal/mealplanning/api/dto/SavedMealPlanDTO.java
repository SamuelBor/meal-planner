package org.sjb.personal.mealplanning.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A previously saved meal plan file")
public record SavedMealPlanDTO(
    @Schema(description = "Name of the file", example = "meal_plan_2023-10-27T10-30-00.txt")
    String fileName,
    @Schema(description = "Content of the meal plan")
    String content
) {}