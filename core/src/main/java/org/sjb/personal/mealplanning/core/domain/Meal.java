package org.sjb.personal.mealplanning.core.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @ElementCollection
    private List<Ingredient> ingredients;
    
    private String recipeUrl;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> allowedDays;

    private int baseServings;
    private boolean jessicaLikes;
    
    @ColumnDefault("false")
    private boolean jessicaSpecialty;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Weather> weatherTags;

    public Meal() {
    }

    public Meal(Long id, String name, List<Ingredient> ingredients, String recipeUrl, Set<DayOfWeek> allowedDays, int baseServings, boolean jessicaLikes, boolean jessicaSpecialty, Set<Weather> weatherTags) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.recipeUrl = recipeUrl;
        this.allowedDays = allowedDays;
        this.baseServings = baseServings;
        this.jessicaLikes = jessicaLikes;
        this.jessicaSpecialty = jessicaSpecialty;
        this.weatherTags = weatherTags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getRecipeUrl() {
        return recipeUrl;
    }

    public void setRecipeUrl(String recipeUrl) {
        this.recipeUrl = recipeUrl;
    }

    public Set<DayOfWeek> getAllowedDays() {
        return allowedDays;
    }

    public void setAllowedDays(Set<DayOfWeek> allowedDays) {
        this.allowedDays = allowedDays;
    }

    public int getBaseServings() {
        return baseServings;
    }

    public void setBaseServings(int baseServings) {
        this.baseServings = baseServings;
    }

    public boolean isJessicaLikes() {
        return jessicaLikes;
    }

    public void setJessicaLikes(boolean jessicaLikes) {
        this.jessicaLikes = jessicaLikes;
    }

    public boolean isJessicaSpecialty() {
        return jessicaSpecialty;
    }

    public void setJessicaSpecialty(boolean jessicaSpecialty) {
        this.jessicaSpecialty = jessicaSpecialty;
    }

    public Set<Weather> getWeatherTags() {
        return weatherTags;
    }

    public void setWeatherTags(Set<Weather> weatherTags) {
        this.weatherTags = weatherTags;
    }
}