package org.sjb.personal.mealplanning.core.service;

import org.sjb.personal.mealplanning.core.domain.FreezerItem;
import org.sjb.personal.mealplanning.core.repository.FreezerItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FreezerService {

    private final FreezerItemRepository freezerItemRepository;

    public FreezerService(FreezerItemRepository freezerItemRepository) {
        this.freezerItemRepository = freezerItemRepository;
    }

    public List<FreezerItem> findAll() {
        return freezerItemRepository.findAll();
    }

    public Optional<FreezerItem> findById(Long id) {
        return freezerItemRepository.findById(id);
    }

    public FreezerItem save(FreezerItem item) {
        return freezerItemRepository.save(item);
    }

    public void deleteById(Long id) {
        freezerItemRepository.deleteById(id);
    }
}