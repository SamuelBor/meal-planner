package org.sjb.personal.mealplanning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sjb.personal.mealplanning.api.dto.DailyMealPlanDTO;
import org.sjb.personal.mealplanning.api.dto.MealPlanRequestDTO;
import org.sjb.personal.mealplanning.api.dto.SavedMealPlanDTO;
import org.sjb.personal.mealplanning.api.dto.WeeklyMealPlanResponseDTO;
import org.sjb.personal.mealplanning.api.mapper.MealMapper;
import org.sjb.personal.mealplanning.core.service.MealPlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meal-plans")
@Tag(name = "Meal Plans", description = "API for generating meal plans")
public class MealPlanController {

    private static final Logger log = LoggerFactory.getLogger(MealPlanController.class);
    private final MealPlanService mealPlanService;
    private final MealMapper mealMapper;

    public MealPlanController(MealPlanService mealPlanService, MealMapper mealMapper) {
        this.mealPlanService = mealPlanService;
        this.mealMapper = mealMapper;
    }

    @Operation(summary = "Generate a weekly meal plan", description = "Generates a meal plan based on attendees for each day")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated meal plan")
    })
    @PostMapping
    public WeeklyMealPlanResponseDTO generateWeeklyPlan(@RequestBody MealPlanRequestDTO request) {
        log.info("Request received to generate weekly meal plan. Weather: {}", request.weather());
        var result = mealPlanService.generateWeeklyPlan(request.weeklyAttendees(), request.weather());
        log.info("Successfully generated weekly meal plan");
        
        List<DailyMealPlanDTO> dailyPlans = result.dailyPlans().stream()
                .map(plan -> new DailyMealPlanDTO(
                        plan.day(),
                        plan.meal() != null ? mealMapper.toDTO(plan.meal()) : null,
                        plan.isLeftovers(),
                        plan.mealName()
                ))
                .collect(Collectors.toList());

        return new WeeklyMealPlanResponseDTO(dailyPlans, result.aggregatedIngredients());
    }

    @Operation(summary = "Get saved meal plans", description = "Retrieves a list of previously generated meal plans from the file system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved saved meal plans")
    })
    @GetMapping("/saved")
    public List<SavedMealPlanDTO> getSavedMealPlans() {
        log.info("Request received to get saved meal plans");
        return mealPlanService.getSavedMealPlans().stream()
                .map(plan -> new SavedMealPlanDTO(plan.fileName(), plan.content()))
                .collect(Collectors.toList());
    }
}