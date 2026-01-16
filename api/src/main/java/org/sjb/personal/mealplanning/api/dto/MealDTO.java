package org.sjb.personal.mealplanning.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sjb.personal.mealplanning.core.domain.Weather;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Schema(description = "Data Transfer Object for Meal")
public record MealDTO(
    @Schema(description = "Unique identifier of the meal", example = "1")
    Long id,
    @Schema(description = "Name of the meal", example = "Spaghetti Bolognese")
    String name,
    @Schema(description = "List of ingredients")
    List<IngredientDTO> ingredients,
    @Schema(description = "URL to the recipe", example = "https://example.com/recipe")
    String recipeUrl,
    @Schema(description = "Days of the week the meal can be cooked", example = "[\"MONDAY\", \"WEDNESDAY\"]")
    Set<DayOfWeek> allowedDays,
    @Schema(description = "Base number of servings", example = "4")
    int baseServings,
    @Schema(description = "Whether Jessica likes this meal", example = "true")
    boolean jessicaLikes,
    @Schema(description = "Whether this is a Jessica specialty (only cooked when both are eating)", example = "false")
    boolean jessicaSpecialty,
    @Schema(description = "Weather conditions suitable for this meal", example = "[\"COLD\", \"NEUTRAL\"]")
    Set<Weather> weatherTags
) {}