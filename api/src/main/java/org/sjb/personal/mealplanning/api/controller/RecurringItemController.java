package org.sjb.personal.mealplanning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sjb.personal.mealplanning.core.domain.RecurringItem;
import org.sjb.personal.mealplanning.core.service.RecurringItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-items")
@Tag(name = "Recurring Items", description = "API for managing recurring shopping list items")
public class RecurringItemController {

    private final RecurringItemService recurringItemService;

    public RecurringItemController(RecurringItemService recurringItemService) {
        this.recurringItemService = recurringItemService;
    }

    @Operation(summary = "Get all recurring items", description = "Retrieves a list of all recurring shopping list items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    @GetMapping
    public List<RecurringItem> getAllRecurringItems() {
        return recurringItemService.getAllRecurringItems();
    }

    @Operation(summary = "Create a recurring item", description = "Creates a new recurring shopping list item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created item")
    })
    @PostMapping
    public RecurringItem createRecurringItem(@RequestBody RecurringItem recurringItem) {
        return recurringItemService.createRecurringItem(recurringItem);
    }

    @Operation(summary = "Update a recurring item", description = "Updates an existing recurring shopping list item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated item"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RecurringItem> updateRecurringItem(@PathVariable Long id, @RequestBody RecurringItem recurringItem) {
        try {
            return ResponseEntity.ok(recurringItemService.updateRecurringItem(id, recurringItem));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a recurring item", description = "Deletes a recurring shopping list item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted item")
    })
    @DeleteMapping("/{id}")
    public void deleteRecurringItem(@PathVariable Long id) {
        recurringItemService.deleteRecurringItem(id);
    }
}
