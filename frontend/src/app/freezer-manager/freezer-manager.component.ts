import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

interface FreezerItem {
  id?: number;
  name: string;
  quantity: number;
  isFullMeal: boolean;
}

@Component({
  selector: 'app-freezer-manager',
  templateUrl: './freezer-manager.component.html',
  styleUrls: ['./freezer-manager.component.scss'],
  standalone: false
})
export class FreezerManagerComponent implements OnInit {
  freezerItems: FreezerItem[] = [];
  itemForm: FormGroup;
  showForm = false;
  loading = false;

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.itemForm = this.fb.group({
      name: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      isFullMeal: [false]
    });
  }

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems() {
    this.loading = true;
    this.http.get<FreezerItem[]>('/api/freezer').subscribe({
      next: (data) => {
        this.freezerItems = data.sort((a, b) => a.name.localeCompare(b.name));
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSubmit() {
    if (this.itemForm.valid) {
      this.loading = true;
      this.http.post<FreezerItem>('/api/freezer', this.itemForm.value).subscribe({
        next: () => {
          this.loadItems();
          this.showForm = false;
          this.itemForm.reset({ quantity: 1, isFullMeal: false });
        },
        error: () => {
          this.loading = false;
        }
      });
    }
  }

  deleteItem(id: number) {
    if(confirm('Are you sure?')) {
      this.loading = true;
      this.http.delete(`/api/freezer/${id}`).subscribe({
        next: () => this.loadItems(),
        error: () => {
          this.loading = false;
        }
      });
    }
  }
}