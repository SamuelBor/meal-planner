import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Ingredient {
  name: string;
  quantity: number;
  unit: string;
}

export interface Meal {
  id?: number;
  name: string;
  ingredients: Ingredient[];
  recipeUrl: string;
  allowedDays: string[];
  baseServings: number;
  jessicaLikes: boolean;
  jessicaSpecialty: boolean;
  weatherTags: string[];
}

@Injectable({
  providedIn: 'root'
})
export class MealService {
  private apiUrl = '/api/meals';

  constructor(private http: HttpClient) {}

  getAllMeals(): Observable<Meal[]> {
    return this.http.get<Meal[]>(this.apiUrl);
  }

  createMeal(meal: Meal): Observable<Meal> {
    return this.http.post<Meal>(this.apiUrl, meal);
  }

  updateMeal(id: number, meal: Meal): Observable<Meal> {
    return this.http.put<Meal>(`${this.apiUrl}/${id}`, meal);
  }

  deleteMeal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}