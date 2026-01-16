package org.sjb.personal.mealplanning.api.mapper;

import org.sjb.personal.mealplanning.api.dto.IngredientDTO;
import org.sjb.personal.mealplanning.api.dto.MealDTO;
import org.sjb.personal.mealplanning.core.domain.Ingredient;
import org.sjb.personal.mealplanning.core.domain.Meal;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MealMapper {

    public MealDTO toDTO(Meal meal) {
        List<IngredientDTO> ingredientDTOs = meal.getIngredients() == null ? Collections.emptyList() :
            meal.getIngredients().stream()
                .map(i -> new IngredientDTO(i.getName(), i.getQuantity(), i.getUnit()))
                .collect(Collectors.toList());

        return new MealDTO(
            meal.getId(),
            meal.getName(),
            ingredientDTOs,
            meal.getRecipeUrl(),
            meal.getAllowedDays(),
            meal.getBaseServings(),
            meal.isJessicaLikes(),
            meal.isJessicaSpecialty(),
            meal.getWeatherTags()
        );
    }

    public Meal toEntity(MealDTO dto) {
        Meal meal = new Meal();
        meal.setId(dto.id());
        meal.setName(dto.name());
        
        List<Ingredient> ingredients = dto.ingredients() == null ? Collections.emptyList() :
            dto.ingredients().stream()
                .map(d -> new Ingredient(d.name(), d.quantity(), d.unit()))
                .collect(Collectors.toList());
        meal.setIngredients(ingredients);

        meal.setRecipeUrl(dto.recipeUrl());
        meal.setAllowedDays(dto.allowedDays());
        meal.setBaseServings(dto.baseServings());
        meal.setJessicaLikes(dto.jessicaLikes());
        meal.setJessicaSpecialty(dto.jessicaSpecialty());
        meal.setWeatherTags(dto.weatherTags());
        return meal;
    }
}