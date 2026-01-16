import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface SavedMealPlan {
  fileName: string;
  content: string;
}

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

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<SavedMealPlan[]>('/api/meal-plans/saved').subscribe({
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
      const dateStr = match[1].replace(/-/g, ':').replace('T', ' ').replace(/:/g, '-').replace(' ', 'T');
      // The above replacement logic is a bit hacky to reverse the filename format back to something readable
      // Let's just return the raw string or try to parse it if needed.
      // Actually, let's just replace the T with a space and the hyphens in time with colons for display
      const parts = match[1].split('T');
      if (parts.length === 2) {
          return `${parts[0]} ${parts[1].replace(/-/g, ':')}`;
      }
      return match[1];
    }
    return fileName;
  }
}