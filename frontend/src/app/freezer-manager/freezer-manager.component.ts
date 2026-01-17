import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FreezerService, FreezerItem } from '../services/freezer.service';

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

  constructor(private freezerService: FreezerService, private fb: FormBuilder) {
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
    this.freezerService.getAllItems().subscribe({
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
      this.freezerService.addItem(this.itemForm.value).subscribe({
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
      this.freezerService.deleteItem(id).subscribe({
        next: () => this.loadItems(),
        error: () => {
          this.loading = false;
        }
      });
    }
  }
}