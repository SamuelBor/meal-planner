package org.sjb.personal.mealplanning.api.mapper;

import org.sjb.personal.mealplanning.api.dto.FreezerItemDTO;
import org.sjb.personal.mealplanning.core.domain.FreezerItem;
import org.springframework.stereotype.Component;

@Component
public class FreezerItemMapper {

    public FreezerItemDTO toDTO(FreezerItem item) {
        return new FreezerItemDTO(
            item.getId(),
            item.getName(),
            item.getQuantity(),
            item.isFullMeal()
        );
    }

    public FreezerItem toEntity(FreezerItemDTO dto) {
        FreezerItem item = new FreezerItem();
        item.setId(dto.id());
        item.setName(dto.name());
        item.setQuantity(dto.quantity());
        item.setFullMeal(dto.isFullMeal());
        return item;
    }
}