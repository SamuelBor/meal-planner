import { Component, OnInit } from '@angular/core';
import { RecurringItem, RecurringItemService } from '../services/recurring-item.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-recurring-items',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recurring-items.component.html',
  styleUrls: ['./recurring-items.component.scss']
})
export class RecurringItemsComponent implements OnInit {
  recurringItems: RecurringItem[] = [];
  newItem: RecurringItem = { name: '', quantity: 1, unit: '' };
  editingItem: RecurringItem | null = null;

  constructor(private recurringItemService: RecurringItemService) { }

  ngOnInit(): void {
    this.loadRecurringItems();
  }

  loadRecurringItems(): void {
    this.recurringItemService.getAllRecurringItems().subscribe(items => {
      this.recurringItems = items;
    });
  }

  addItem(): void {
    if (this.newItem.name && this.newItem.quantity > 0) {
      this.recurringItemService.createRecurringItem(this.newItem).subscribe(item => {
        this.recurringItems.push(item);
        this.newItem = { name: '', quantity: 1, unit: '' };
      });
    }
  }

  editItem(item: RecurringItem): void {
    this.editingItem = { ...item };
  }

  cancelEdit(): void {
    this.editingItem = null;
  }

  saveEdit(): void {
    if (this.editingItem && this.editingItem.id) {
      this.recurringItemService.updateRecurringItem(this.editingItem.id, this.editingItem).subscribe(updatedItem => {
        const index = this.recurringItems.findIndex(i => i.id === updatedItem.id);
        if (index !== -1) {
          this.recurringItems[index] = updatedItem;
        }
        this.editingItem = null;
      });
    }
  }

  deleteItem(id: number): void {
    if (confirm('Are you sure you want to delete this item?')) {
      this.recurringItemService.deleteRecurringItem(id).subscribe(() => {
        this.recurringItems = this.recurringItems.filter(i => i.id !== id);
      });
    }
  }
}
