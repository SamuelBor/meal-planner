package org.sjb.personal.mealplanning.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ingredient details")
public record IngredientDTO(
    @Schema(description = "Name of the ingredient", example = "Chicken Breast")
    String name,
    @Schema(description = "Quantity needed", example = "4")
    double quantity,
    @Schema(description = "Unit of measurement (optional)", example = "pcs")
    String unit
) {}