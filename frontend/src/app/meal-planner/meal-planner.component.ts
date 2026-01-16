import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Ingredient {
  name: string;
  quantity: number;
  unit: string;
}

interface DailyPlan {
  dayOfWeek: string;
  meal: {
    name: string;
    recipeUrl?: string;
    ingredients?: Ingredient[];
  } | null;
  isLeftovers: boolean;
  mealName: string;
}

interface WeeklyPlanResponse {
  dailyPlans: DailyPlan[];
  aggregatedIngredients: { [key: string]: number };
}

@Component({
  selector: 'app-meal-planner',
  templateUrl: './meal-planner.component.html',
  styleUrls: ['./meal-planner.component.scss'],
  standalone: false
})
export class MealPlannerComponent {
  daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
  weatherOptions = ['HOT', 'NEUTRAL', 'COLD'];

  // State for attendees: day -> { Samuel: boolean, Jessica: boolean }
  attendees: { [key: string]: { Samuel: boolean, Jessica: boolean } } = {};

  selectedWeather: string = 'NEUTRAL';

  weeklyPlan: WeeklyPlanResponse | null = null;
  loading = false;
  error: string | null = null;

  constructor(private http: HttpClient) {
    // Initialize defaults: both eating every day
    this.daysOfWeek.forEach(day => {
      this.attendees[day] = { Samuel: true, Jessica: true };
    });
  }

  generatePlan() {
    this.loading = true;
    this.error = null;
    this.weeklyPlan = null;

    // Build request payload
    const weeklyAttendees: { [key: string]: string[] } = {};

    this.daysOfWeek.forEach(day => {
      const dayAttendees = [];
      if (this.attendees[day].Samuel) dayAttendees.push('Samuel');
      if (this.attendees[day].Jessica) dayAttendees.push('Jessica');
      weeklyAttendees[day] = dayAttendees;
    });

    const request = {
        weeklyAttendees,
        weather: this.selectedWeather
    };

    this.http.post<WeeklyPlanResponse>('/api/meal-plans', request).subscribe({
      next: (response) => {
        this.weeklyPlan = response;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error generating plan', err);
        this.error = 'Failed to generate meal plan. Please try again.';
        this.loading = false;
      }
    });
  }

  getAggregatedIngredientsKeys(): string[] {
    if (!this.weeklyPlan || !this.weeklyPlan.aggregatedIngredients) return [];
    return Object.keys(this.weeklyPlan.aggregatedIngredients);
  }
}