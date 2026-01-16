package org.sjb.personal.mealplanning.core.repository;

import org.sjb.personal.mealplanning.core.domain.FreezerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreezerItemRepository extends JpaRepository<FreezerItem, Long> {
}