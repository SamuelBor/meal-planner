import { Component, OnInit } from '@angular/core';
import { MealPlanService, SavedMealPlan } from '../services/meal-plan.service';

@Component({
  selector: 'app-saved-plans',
  templateUrl: './saved-plans.component.html',
  styleUrls: ['./saved-plans.component.scss'],
  standalone: false
})
export class SavedPlansComponent implements OnInit {
  savedPlans: SavedMealPlan[] = [];
  selectedPlan: SavedMealPlan | null = null;
  loading = true;

  constructor(private mealPlanService: MealPlanService) {}

  ngOnInit(): void {
    this.mealPlanService.getSavedPlans().subscribe({
      next: (data) => {
        this.savedPlans = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load saved plans', err);
        this.loading = false;
      }
    });
  }

  selectPlan(plan: SavedMealPlan) {
    this.selectedPlan = plan;
  }

  formatDate(fileName: string): string {
    // Extract timestamp from filename: meal_plan_2023-10-27T10-30-00.txt
    const match = fileName.match(/meal_plan_(.*)\.txt/);
    if (match && match[1]) {
      const parts = match[1].split('T');
      if (parts.length === 2) {
          return `${parts[0]} ${parts[1].replace(/-/g, ':')}`;
      }
      return match[1];
    }
    return fileName;
  }
}