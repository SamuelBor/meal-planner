package org.sjb.personal.mealplanning.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for Freezer Item")
public record FreezerItemDTO(
    @Schema(description = "Unique identifier of the freezer item", example = "1")
    Long id,
    @Schema(description = "Name of the item", example = "Chicken Thighs")
    String name,
    @Schema(description = "Quantity of the item", example = "4")
    int quantity,
    @Schema(description = "Whether this item is a full meal (ready to eat) or an ingredient", example = "false")
    boolean isFullMeal
) {}