package org.sjb.personal.mealplanning.core.service;

import org.sjb.personal.mealplanning.core.domain.RecurringItem;
import org.sjb.personal.mealplanning.core.repository.RecurringItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RecurringItemService {

    private final RecurringItemRepository recurringItemRepository;

    public RecurringItemService(RecurringItemRepository recurringItemRepository) {
        this.recurringItemRepository = recurringItemRepository;
    }

    public List<RecurringItem> getAllRecurringItems() {
        return recurringItemRepository.findAll();
    }

    public RecurringItem createRecurringItem(RecurringItem recurringItem) {
        return recurringItemRepository.save(recurringItem);
    }

    public Optional<RecurringItem> getRecurringItemById(Long id) {
        return recurringItemRepository.findById(id);
    }

    public RecurringItem updateRecurringItem(Long id, RecurringItem recurringItemDetails) {
        RecurringItem recurringItem = recurringItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RecurringItem not found with id " + id));

        recurringItem.setName(recurringItemDetails.getName());
        recurringItem.setQuantity(recurringItemDetails.getQuantity());
        recurringItem.setUnit(recurringItemDetails.getUnit());

        return recurringItemRepository.save(recurringItem);
    }

    public void deleteRecurringItem(Long id) {
        recurringItemRepository.deleteById(id);
    }
}
