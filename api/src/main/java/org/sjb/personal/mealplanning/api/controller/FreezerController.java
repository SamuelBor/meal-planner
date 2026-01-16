package org.sjb.personal.mealplanning.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sjb.personal.mealplanning.api.dto.FreezerItemDTO;
import org.sjb.personal.mealplanning.api.mapper.FreezerItemMapper;
import org.sjb.personal.mealplanning.core.service.FreezerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/freezer")
@Tag(name = "Freezer", description = "The Freezer API")
public class FreezerController {

    private static final Logger log = LoggerFactory.getLogger(FreezerController.class);
    private final FreezerService freezerService;
    private final FreezerItemMapper freezerItemMapper;

    public FreezerController(FreezerService freezerService, FreezerItemMapper freezerItemMapper) {
        this.freezerService = freezerService;
        this.freezerItemMapper = freezerItemMapper;
    }

    @Operation(summary = "Get all freezer items", description = "Returns a list of all items in the freezer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    })
    @GetMapping
    public List<FreezerItemDTO> getAllItems() {
        log.info("Request received to get all freezer items");
        return freezerService.findAll().stream()
                .map(freezerItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Add an item to the freezer", description = "Adds a new item to the freezer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added item")
    })
    @PostMapping
    public FreezerItemDTO addItem(@RequestBody FreezerItemDTO itemDTO) {
        log.info("Request received to add freezer item: {}", itemDTO.name());
        var item = freezerItemMapper.toEntity(itemDTO);
        var savedItem = freezerService.save(item);
        log.info("Successfully added freezer item with ID: {}", savedItem.getId());
        return freezerItemMapper.toDTO(savedItem);
    }

    @Operation(summary = "Delete an item from the freezer", description = "Deletes an item identified by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted item")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "ID of the item to delete", required = true)
            @PathVariable Long id) {
        log.info("Request received to delete freezer item with ID: {}", id);
        freezerService.deleteById(id);
        log.info("Successfully deleted freezer item with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}