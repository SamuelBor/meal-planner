package org.sjb.personal.mealplanning.core.service;

import org.sjb.personal.mealplanning.core.domain.FreezerItem;
import org.sjb.personal.mealplanning.core.domain.Ingredient;
import org.sjb.personal.mealplanning.core.domain.Meal;
import org.sjb.personal.mealplanning.core.domain.Weather;
import org.sjb.personal.mealplanning.core.repository.FreezerItemRepository;
import org.sjb.personal.mealplanning.core.repository.MealRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class MealPlanService {

    private final MealRepository mealRepository;
    private final FreezerItemRepository freezerItemRepository;
    private final Random random = new Random();
    private static final String MEAL_PLANS_DIRECTORY = System.getProperty("user.home") + "/Documents/mealplans";

    public MealPlanService(MealRepository mealRepository, FreezerItemRepository freezerItemRepository) {
        this.mealRepository = mealRepository;
        this.freezerItemRepository = freezerItemRepository;
    }

    public record DailyPlan(DayOfWeek day, Meal meal, boolean isLeftovers, String mealName) {}
    public record WeeklyPlanResult(List<DailyPlan> dailyPlans, Map<String, Double> aggregatedIngredients) {}
    public record SavedMealPlan(String fileName, String content) {}

    public WeeklyPlanResult generateWeeklyPlan(Map<DayOfWeek, List<String>> weeklyAttendees, Weather weather) {
        List<Meal> allMeals = mealRepository.findAll();
        List<FreezerItem> freezerItems = freezerItemRepository.findAll();
        List<DailyPlan> dailyPlans = new ArrayList<>();
        Map<String, Double> aggregatedIngredients = new HashMap<>();
        Set<Long> usedMealIds = new HashSet<>();
        
        // Track leftovers: Meal -> remaining portions
        Map<Meal, Integer> leftovers = new HashMap<>();

        // Process days in order: Sunday to Saturday
        List<DayOfWeek> days = new ArrayList<>();
        days.add(DayOfWeek.SUNDAY);
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.TUESDAY);
        days.add(DayOfWeek.WEDNESDAY);
        days.add(DayOfWeek.THURSDAY);
        days.add(DayOfWeek.FRIDAY);
        days.add(DayOfWeek.SATURDAY);
        
        boolean treatNightUsed = false;
        
        // Keep track of recently used meal names to avoid similarity
        List<String> recentMealNames = new ArrayList<>();

        for (DayOfWeek day : days) {
            List<String> attendees = weeklyAttendees.getOrDefault(day, Collections.emptyList());
            int portionsNeeded = attendees.size();

            if (portionsNeeded == 0) {
                dailyPlans.add(new DailyPlan(day, null, false, "No meal needed"));
                continue;
            }

            // Special logic for Jessica eating alone
            if (portionsNeeded == 1 && attendees.contains("Jessica")) {
                Optional<Meal> leftoverMeal = findSuitableLeftover(leftovers, portionsNeeded);
                if (leftoverMeal.isPresent()) {
                    Meal meal = leftoverMeal.get();
                    int remaining = leftovers.get(meal);
                    leftovers.put(meal, remaining - portionsNeeded);
                    if (leftovers.get(meal) <= 0) {
                        leftovers.remove(meal);
                    }
                    dailyPlans.add(new DailyPlan(day, meal, true, meal.getName() + " (Leftovers)"));
                    recentMealNames.add(meal.getName());
                } else {
                    // No leftovers, Jessica sorts herself out
                    dailyPlans.add(new DailyPlan(day, null, false, "Jessica sorting her dinner"));
                }
                continue; // Skip to next day
            }

            // Treat Night Logic (Friday or Saturday)
            if (!treatNightUsed && (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY)) {
                // Check if both are eating
                if (attendees.contains("Samuel") && attendees.contains("Jessica")) {
                    // 33% chance
                    if (random.nextDouble() < 0.33) {
                        String treat = random.nextBoolean() ? "Meal Out" : "Order Takeaway";
                        dailyPlans.add(new DailyPlan(day, null, false, treat));
                        treatNightUsed = true;
                        continue;
                    }
                }
            }

            // Try to use leftovers first (for other scenarios)
            Optional<Meal> leftoverMeal = findSuitableLeftover(leftovers, portionsNeeded);
            
            if (leftoverMeal.isPresent()) {
                Meal meal = leftoverMeal.get();
                int remaining = leftovers.get(meal);
                leftovers.put(meal, remaining - portionsNeeded);
                if (leftovers.get(meal) <= 0) {
                    leftovers.remove(meal);
                }
                dailyPlans.add(new DailyPlan(day, meal, true, meal.getName() + " (Leftovers)"));
                recentMealNames.add(meal.getName());
            } else {
                // Cook a new meal or use freezer meal
                Optional<Meal> selectedMealOpt = selectMeal(allMeals, freezerItems, usedMealIds, day, portionsNeeded, attendees, weather, recentMealNames);
                
                if (selectedMealOpt.isPresent()) {
                    Meal meal = selectedMealOpt.get();
                    
                    // If it's a real meal entity (has ID), mark as used
                    if (meal.getId() != null) {
                        usedMealIds.add(meal.getId());
                    }
                    
                    // Calculate leftovers
                    int cookedPortions = meal.getBaseServings();
                    int remainingPortions = cookedPortions - portionsNeeded;
                    if (remainingPortions > 0) {
                        leftovers.put(meal, remainingPortions);
                    }

                    // Add ingredients to aggregation (only if it's not a freezer full meal)
                    // We assume freezer full meals don't need shopping for ingredients
                    if (meal.getIngredients() != null && !isFreezerFullMeal(meal, freezerItems)) {
                        for (Ingredient ingredient : meal.getIngredients()) {
                            // Check if this ingredient is in the freezer and use it if possible
                            double quantityNeeded = ingredient.getQuantity();
                            
                            if (portionsNeeded == 1 && attendees.contains("Samuel") && !attendees.contains("Jessica")) {
                                quantityNeeded = quantityNeeded / 2.0;
                            }

                            Optional<FreezerItem> freezerIngredient = findFreezerIngredient(freezerItems, ingredient.getName());
                            
                            if (freezerIngredient.isPresent()) {
                                FreezerItem item = freezerIngredient.get();
                                if (item.getQuantity() >= quantityNeeded) {
                                    // Use from freezer
                                    item.setQuantity((int) (item.getQuantity() - quantityNeeded));
                                    if (item.getQuantity() <= 0) {
                                        freezerItems.remove(item);
                                        freezerItemRepository.delete(item); // Remove from DB
                                    } else {
                                        freezerItemRepository.save(item); // Update DB
                                    }
                                    quantityNeeded = 0; // Fully covered
                                } else {
                                    // Partially use from freezer
                                    quantityNeeded -= item.getQuantity();
                                    freezerItems.remove(item);
                                    freezerItemRepository.delete(item); // Remove from DB
                                }
                            }

                            if (quantityNeeded > 0) {
                                String key = ingredient.getName() + (ingredient.getUnit() != null ? " (" + ingredient.getUnit() + ")" : "");
                                aggregatedIngredients.merge(key, quantityNeeded, Double::sum);
                            }
                        }
                        
                        // Adjust leftovers if we scaled down
                        if (portionsNeeded == 1 && attendees.contains("Samuel") && !attendees.contains("Jessica")) {
                             int scaledCooked = cookedPortions / 2; 
                             int actualLeftovers = scaledCooked - portionsNeeded; 
                             
                             if (actualLeftovers > 0) {
                                 leftovers.put(meal, actualLeftovers);
                             } else {
                                 leftovers.remove(meal);
                             }
                        }
                    } else if (isFreezerFullMeal(meal, freezerItems)) {
                        // If it's a full meal from freezer, decrement its quantity
                        Optional<FreezerItem> freezerMeal = freezerItems.stream()
                                .filter(item -> item.isFullMeal() && item.getName().equals(meal.getName().replace(" (From Freezer)", "")))
                                .findFirst();
                        
                        if (freezerMeal.isPresent()) {
                            FreezerItem item = freezerMeal.get();
                            int consumed = Math.min(item.getQuantity(), portionsNeeded);
                            item.setQuantity(item.getQuantity() - consumed);
                            
                            if (item.getQuantity() <= 0) {
                                freezerItems.remove(item);
                                freezerItemRepository.delete(item);
                            } else {
                                freezerItemRepository.save(item);
                            }
                        }
                    }
                    
                    dailyPlans.add(new DailyPlan(day, meal, false, meal.getName()));
                    recentMealNames.add(meal.getName());
                } else {
                    dailyPlans.add(new DailyPlan(day, null, false, "No suitable meal found"));
                }
            }
        }

        writePlanToFile(dailyPlans, aggregatedIngredients);

        return new WeeklyPlanResult(dailyPlans, aggregatedIngredients);
    }

    public void saveModifiedPlan(List<DailyPlan> dailyPlans, Map<String, Double> aggregatedIngredients) {
        writePlanToFile(dailyPlans, aggregatedIngredients);
    }

    public List<SavedMealPlan> getSavedMealPlans() {
        Path directoryPath = Paths.get(MEAL_PLANS_DIRECTORY);
        if (!Files.exists(directoryPath)) {
            return Collections.emptyList();
        }

        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .sorted(Comparator.comparingLong((Path p) -> p.toFile().lastModified()).reversed())
                    .map(path -> {
                        try {
                            String content = Files.readString(path);
                            return new SavedMealPlan(path.getFileName().toString(), content);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private boolean isFreezerFullMeal(Meal meal, List<FreezerItem> freezerItems) {
        return freezerItems.stream()
                .anyMatch(item -> item.isFullMeal() && item.getName().equals(meal.getName().replace(" (From Freezer)", "")));
    }
    
    private Optional<FreezerItem> findFreezerIngredient(List<FreezerItem> freezerItems, String ingredientName) {
        return freezerItems.stream()
                .filter(item -> !item.isFullMeal())
                .filter(item -> ingredientName.toLowerCase().contains(item.getName().toLowerCase()) || 
                                item.getName().toLowerCase().contains(ingredientName.toLowerCase()))
                .findFirst();
    }

    private Optional<Meal> findSuitableLeftover(Map<Meal, Integer> leftovers, int portionsNeeded) {
        return leftovers.entrySet().stream()
                .filter(entry -> entry.getValue() >= portionsNeeded)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private Optional<Meal> selectMeal(List<Meal> allMeals, List<FreezerItem> freezerItems, Set<Long> usedMealIds, DayOfWeek day, int portionsNeeded, List<String> attendees, Weather weather, List<String> recentMealNames) {
        boolean jessicaEating = attendees.contains("Jessica");
        boolean samuelEating = attendees.contains("Samuel");

        List<Meal> candidates = allMeals.stream()
                .filter(meal -> !usedMealIds.contains(meal.getId()))
                .filter(meal -> meal.getAllowedDays() == null || meal.getAllowedDays().isEmpty() || meal.getAllowedDays().contains(day))
                .filter(meal -> meal.getBaseServings() >= portionsNeeded)
                .filter(meal -> !isTooSimilar(meal.getName(), recentMealNames))
                .collect(Collectors.toList());

        // Filter out meals that result in < 1 Pack if scaling down for Samuel only
        if (portionsNeeded == 1 && samuelEating && !jessicaEating) {
            candidates = candidates.stream()
                    .filter(meal -> {
                        if (meal.getIngredients() == null) return true;
                        return meal.getIngredients().stream()
                                .noneMatch(ing -> 
                                    ing.getUnit() != null && 
                                    ing.getUnit().equalsIgnoreCase("Pack") && 
                                    (ing.getQuantity() / 2.0) < 1.0
                                );
                    })
                    .collect(Collectors.toList());
        }

        // Filter by weather if specified
        if (weather != null) {
            List<Meal> weatherFiltered = candidates.stream()
                    .filter(meal -> meal.getWeatherTags() != null && meal.getWeatherTags().contains(weather))
                    .collect(Collectors.toList());
            if (!weatherFiltered.isEmpty()) {
                candidates = weatherFiltered;
            }
        }

        // Filter based on Jessica's preference if she is eating
        if (jessicaEating) {
            // Standard Jessica Likes filtering
            List<Meal> jessicaLikesCandidates = candidates.stream()
                    .filter(Meal::isJessicaLikes)
                    .collect(Collectors.toList());
            if (!jessicaLikesCandidates.isEmpty()) {
                candidates = jessicaLikesCandidates;
            }
        } else {
            List<Meal> jessicaDislikesCandidates = candidates.stream()
                    .filter(meal -> !meal.isJessicaLikes())
                    .collect(Collectors.toList());
            
            if (!jessicaDislikesCandidates.isEmpty()) {
                if (random.nextDouble() < 0.3) {
                    candidates = jessicaDislikesCandidates;
                }
            }
        }
        
        // Ensure we don't pick a specialty if only Samuel is eating (unless it's the only option)
        if (portionsNeeded == 1 && samuelEating && !jessicaEating) {
             List<Meal> nonSpecialties = candidates.stream()
                     .filter(meal -> !meal.isJessicaSpecialty())
                     .collect(Collectors.toList());
             if (!nonSpecialties.isEmpty()) {
                 candidates = nonSpecialties;
             }
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        
        // Check if we should prioritize freezer items (50% chance)
        if (random.nextBoolean() && !freezerItems.isEmpty()) {
            // Check for full meals in freezer first
            List<FreezerItem> fullMealsInFreezer = freezerItems.stream()
                    .filter(FreezerItem::isFullMeal)
                    .filter(item -> item.getQuantity() >= portionsNeeded)
                    .filter(item -> !isTooSimilar(item.getName(), recentMealNames)) // Check similarity for freezer meals too
                    .collect(Collectors.toList());

            if (!fullMealsInFreezer.isEmpty() && random.nextBoolean()) {
                FreezerItem item = fullMealsInFreezer.get(random.nextInt(fullMealsInFreezer.size()));
                Meal freezerMeal = new Meal();
                freezerMeal.setName(item.getName() + " (From Freezer)");
                freezerMeal.setBaseServings(item.getQuantity());
                return Optional.of(freezerMeal);
            }

            // Check for ingredients in freezer
            List<Meal> freezerIngredientCandidates = candidates.stream()
                    .filter(meal -> mealUsesFreezerIngredient(meal, freezerItems))
                    .collect(Collectors.toList());
            
            if (!freezerIngredientCandidates.isEmpty()) {
                return Optional.of(freezerIngredientCandidates.get(random.nextInt(freezerIngredientCandidates.size())));
            }
        }

        // Fallback to random selection from filtered candidates
        return Optional.of(candidates.get(random.nextInt(candidates.size())));
    }
    
    private boolean isTooSimilar(String mealName, List<String> recentMealNames) {
        if (recentMealNames.isEmpty()) {
            return false;
        }
        
        // Get the last meal name (most recent)
        String lastMealName = recentMealNames.get(recentMealNames.size() - 1);
        
        // Words to ignore
        Set<String> ignoredWords = Set.of("chicken", "rice", "noodles", "with", "and", "&", "a", "the");
        
        Set<String> currentWords = Arrays.stream(mealName.toLowerCase().split("\\s+"))
                .filter(w -> !ignoredWords.contains(w))
                .collect(Collectors.toSet());
                
        Set<String> lastWords = Arrays.stream(lastMealName.toLowerCase().split("\\s+"))
                .filter(w -> !ignoredWords.contains(w))
                .collect(Collectors.toSet());
        
        // Check for intersection
        for (String word : currentWords) {
            if (lastWords.contains(word)) {
                return true; // Found a similar word
            }
        }
        
        return false;
    }

    private boolean mealUsesFreezerIngredient(Meal meal, List<FreezerItem> freezerItems) {
        if (meal.getIngredients() == null) {
            return false;
        }
        for (Ingredient ingredient : meal.getIngredients()) {
            for (FreezerItem item : freezerItems) {
                if (!item.isFullMeal()) { // Only check ingredients
                    // Simple string matching
                    if (ingredient.getName().toLowerCase().contains(item.getName().toLowerCase()) || 
                        item.getName().toLowerCase().contains(ingredient.getName().toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void writePlanToFile(List<DailyPlan> dailyPlans, Map<String, Double> aggregatedIngredients) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
        String fileName = "meal_plan_" + timestamp + ".txt";
        Path path = Paths.get(MEAL_PLANS_DIRECTORY);

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.resolve(fileName).toFile()))) {
                writer.write("Weekly Meal Plan\n");
                writer.write("================\n\n");
                for (DailyPlan plan : dailyPlans) {
                    writer.write(plan.day() + ": " + plan.mealName() + "\n");
                }
                
                writer.write("\nShopping List\n");
                writer.write("=============\n\n");
                if (aggregatedIngredients.isEmpty()) {
                    writer.write("No ingredients needed.\n");
                } else {
                    for (Map.Entry<String, Double> entry : aggregatedIngredients.entrySet()) {
                        writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}