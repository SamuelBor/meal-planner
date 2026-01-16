import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, FormArray, Validators, FormControl } from '@angular/forms';

interface Ingredient {
  name: string;
  quantity: number;
  unit: string;
}

interface Meal {
  id?: number;
  name: string;
  ingredients: Ingredient[];
  recipeUrl: string;
  allowedDays: string[];
  baseServings: number;
  jessicaLikes: boolean;
  weatherTags: string[];
}

@Component({
  selector: 'app-meal-manager',
  templateUrl: './meal-manager.component.html',
  styleUrls: ['./meal-manager.component.scss'],
  standalone: false
})
export class MealManagerComponent implements OnInit {
  meals: Meal[] = [];
  mealForm: FormGroup;
  showForm = false;
  editingMealId: number | null = null;
  daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
  weatherOptions = ['HOT', 'NEUTRAL', 'COLD'];
  loading = false;

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.mealForm = this.fb.group({
      name: ['', Validators.required],
      recipeUrl: [''],
      baseServings: [4, [Validators.required, Validators.min(1)]],
      jessicaLikes: [true],
      allowedDays: this.fb.array([]),
      weatherTags: this.fb.array([]),
      ingredients: this.fb.array([])
    });

    this.initFormArrays();
  }

  initFormArrays() {
      // Clear existing controls if any
      while (this.allowedDays.length !== 0) {
          this.allowedDays.removeAt(0);
      }
      while (this.weatherTags.length !== 0) {
          this.weatherTags.removeAt(0);
      }

      // Initialize allowedDays with all days selected by default
      this.daysOfWeek.forEach(() => this.allowedDays.push(new FormControl(true)));
      // Initialize weatherTags with all unselected by default
      this.weatherOptions.forEach(() => this.weatherTags.push(new FormControl(false)));
  }

  ngOnInit(): void {
    this.loadMeals();
  }

  loadMeals() {
    this.loading = true;
    this.http.get<Meal[]>('/api/meals').subscribe({
      next: (data) => {
        this.meals = data.sort((a, b) => a.name.localeCompare(b.name));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  get ingredients() {
    return this.mealForm.get('ingredients') as FormArray;
  }

  get allowedDays() {
      return this.mealForm.get('allowedDays') as FormArray;
  }

  get weatherTags() {
      return this.mealForm.get('weatherTags') as FormArray;
  }

  addIngredient(name: string = '', quantity: number = 1, unit: string = '') {
    const ingredientGroup = this.fb.group({
      name: [name, Validators.required],
      quantity: [quantity, [Validators.required, Validators.min(0)]],
      unit: [unit]
    });
    this.ingredients.push(ingredientGroup);
  }

  removeIngredient(index: number) {
    this.ingredients.removeAt(index);
  }

  startEdit(meal: Meal) {
      this.editingMealId = meal.id!;
      this.showForm = true;

      // Reset form arrays
      this.ingredients.clear();
      while (this.allowedDays.length !== 0) {
          this.allowedDays.removeAt(0);
      }
      while (this.weatherTags.length !== 0) {
          this.weatherTags.removeAt(0);
      }

      // Populate basic fields
      this.mealForm.patchValue({
          name: meal.name,
          recipeUrl: meal.recipeUrl,
          baseServings: meal.baseServings,
          jessicaLikes: meal.jessicaLikes
      });

      // Populate ingredients
      if (meal.ingredients) {
          meal.ingredients.forEach(ing => this.addIngredient(ing.name, ing.quantity, ing.unit));
      }

      // Populate allowedDays
      this.daysOfWeek.forEach(day => {
          const isAllowed = !meal.allowedDays || meal.allowedDays.length === 0 || meal.allowedDays.includes(day);
          this.allowedDays.push(new FormControl(isAllowed));
      });

      // Populate weatherTags
      this.weatherOptions.forEach(weather => {
          const isSelected = meal.weatherTags && meal.weatherTags.includes(weather);
          this.weatherTags.push(new FormControl(isSelected));
      });

      // Scroll to top
      window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit() {
      this.showForm = false;
      this.editingMealId = null;
      this.mealForm.reset({ baseServings: 4, jessicaLikes: true });
      this.ingredients.clear();
      this.initFormArrays();
  }

  onSubmit() {
    if (this.mealForm.valid) {
      this.loading = true;
      const formValue = this.mealForm.value;

      // Convert boolean array back to string array of days
      const selectedDays = formValue.allowedDays
        .map((checked: boolean, i: number) => checked ? this.daysOfWeek[i] : null)
        .filter((v: string | null) => v !== null);

      // Convert boolean array back to string array of weather tags
      const selectedWeather = formValue.weatherTags
        .map((checked: boolean, i: number) => checked ? this.weatherOptions[i] : null)
        .filter((v: string | null) => v !== null);

      const payload = {
          ...formValue,
          allowedDays: selectedDays,
          weatherTags: selectedWeather
      };

      if (this.editingMealId) {
          this.http.put<Meal>(`/api/meals/${this.editingMealId}`, payload).subscribe({
              next: () => {
                  this.loadMeals();
                  this.cancelEdit();
              },
              error: () => {
                  this.loading = false;
              }
          });
      } else {
          this.http.post<Meal>('/api/meals', payload).subscribe({
              next: () => {
                  this.loadMeals();
                  this.cancelEdit();
              },
              error: () => {
                  this.loading = false;
              }
          });
      }
    }
  }

  deleteMeal(id: number) {
      if(confirm('Are you sure?')) {
          this.loading = true;
          this.http.delete(`/api/meals/${id}`).subscribe({
              next: () => this.loadMeals(),
              error: () => {
                  this.loading = false;
              }
          });
      }
  }
}