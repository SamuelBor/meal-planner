package org.sjb.personal.mealplanning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sjb.personal.mealplanning.api.dto.MealDTO;
import org.sjb.personal.mealplanning.api.mapper.MealMapper;
import org.sjb.personal.mealplanning.core.service.MealService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meals")
@Tag(name = "Meals", description = "The Meal API")
public class MealController {

    private static final Logger log = LoggerFactory.getLogger(MealController.class);
    private final MealService mealService;
    private final MealMapper mealMapper;

    public MealController(MealService mealService, MealMapper mealMapper) {
        this.mealService = mealService;
        this.mealMapper = mealMapper;
    }

    @Operation(summary = "Get all meals", description = "Returns a list of all available meals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    @GetMapping
    public List<MealDTO> getAllMeals() {
        log.info("Request received to get all meals");
        return mealService.findAll().stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get a meal by ID", description = "Returns a single meal identified by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved meal"),
            @ApiResponse(responseCode = "404", description = "Meal not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MealDTO> getMealById(
            @Parameter(name = "id", description = "ID of the meal to retrieve", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id) {
        log.info("Request received to get meal by ID: {}", id);
        return mealService.findById(id)
                .map(mealMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new meal", description = "Creates a new meal and returns the created meal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created meal")
    })
    @PostMapping
    public MealDTO createMeal(@RequestBody MealDTO mealDTO) {
        log.info("Request received to create new meal: {}", mealDTO.name());
        var meal = mealMapper.toEntity(mealDTO);
        var savedMeal = mealService.save(meal);
        log.info("Successfully created meal with ID: {}", savedMeal.getId());
        return mealMapper.toDTO(savedMeal);
    }

    @Operation(summary = "Update an existing meal", description = "Updates a meal identified by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated meal"),
            @ApiResponse(responseCode = "404", description = "Meal not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MealDTO> updateMeal(
            @Parameter(name = "id", description = "ID of the meal to update", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id,
            @RequestBody MealDTO mealDTO) {
        log.info("Request received to update meal with ID: {}", id);
        return mealService.findById(id)
                .map(existingMeal -> {
                    var meal = mealMapper.toEntity(mealDTO);
                    meal.setId(id);
                    var updatedMeal = mealService.save(meal);
                    log.info("Successfully updated meal with ID: {}", id);
                    return ResponseEntity.ok(mealMapper.toDTO(updatedMeal));
                })
                .orElseGet(() -> {
                    log.warn("Meal with ID: {} not found for update", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Delete a meal", description = "Deletes a meal identified by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted meal")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(
            @Parameter(name = "id", description = "ID of the meal to delete", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id) {
        log.info("Request received to delete meal with ID: {}", id);
        mealService.deleteById(id);
        log.info("Successfully deleted meal with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}