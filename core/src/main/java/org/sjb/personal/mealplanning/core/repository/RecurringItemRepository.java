package org.sjb.personal.mealplanning.core.repository;

import org.sjb.personal.mealplanning.core.domain.RecurringItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringItemRepository extends JpaRepository<RecurringItem, Long> {
}
