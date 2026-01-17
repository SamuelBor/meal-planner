import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ingredient } from './meal.service';

export interface DailyPlan {
  dayOfWeek: string;
  meal: {
    name: string;
    recipeUrl?: string;
    ingredients?: Ingredient[];
  } | null;
  isLeftovers: boolean;
  mealName: string;
}

export interface WeeklyPlanResponse {
  dailyPlans: DailyPlan[];
  aggregatedIngredients: { [key: string]: number };
}

export interface SavedMealPlan {
  fileName: string;
  content: string;
}

@Injectable({
  providedIn: 'root'
})
export class MealPlanService {
  private apiUrl = '/api/meal-plans';

  constructor(private http: HttpClient) {}

  generatePlan(request: any): Observable<WeeklyPlanResponse> {
    return this.http.post<WeeklyPlanResponse>(this.apiUrl, request);
  }

  savePlan(plan: WeeklyPlanResponse): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/save`, plan);
  }

  getSavedPlans(): Observable<SavedMealPlan[]> {
    return this.http.get<SavedMealPlan[]>(`${this.apiUrl}/saved`);
  }
}