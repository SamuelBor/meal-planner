import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { MealManagerComponent } from './meal-manager/meal-manager.component';
import { MealPlannerComponent } from './meal-planner/meal-planner.component';
import { SavedPlansComponent } from './saved-plans/saved-plans.component';
import { FreezerManagerComponent } from './freezer-manager/freezer-manager.component';
import { RecurringItemsComponent } from './recurring-items/recurring-items.component';

const routes: Routes = [
  { path: 'meals', component: MealManagerComponent },
  { path: 'planner', component: MealPlannerComponent },
  { path: 'saved-plans', component: SavedPlansComponent },
  { path: 'freezer', component: FreezerManagerComponent },
  { path: 'recurring-items', component: RecurringItemsComponent },
  { path: '', redirectTo: '/planner', pathMatch: 'full' }
];

@NgModule({
  declarations: [
    AppComponent,
    MealManagerComponent,
    MealPlannerComponent,
    SavedPlansComponent,
    FreezerManagerComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    RecurringItemsComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }