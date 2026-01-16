package org.sjb.personal.mealplanning.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sjb.personal.mealplanning.core.domain.Weather;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Schema(description = "Request object for generating a weekly meal plan")
public record MealPlanRequestDTO(
    @Schema(description = "Map of day of week to list of attendees (e.g., 'MONDAY': ['Samuel', 'Jessica'])")
    Map<DayOfWeek, List<String>> weeklyAttendees,
    @Schema(description = "Predicted weather for the week", example = "COLD")
    Weather weather
) {}