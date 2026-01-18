import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RecurringItem {
  id?: number;
  name: string;
  quantity: number;
  unit: string;
}

@Injectable({
  providedIn: 'root'
})
export class RecurringItemService {
  private apiUrl = '/api/recurring-items';

  constructor(private http: HttpClient) { }

  getAllRecurringItems(): Observable<RecurringItem[]> {
    return this.http.get<RecurringItem[]>(this.apiUrl);
  }

  createRecurringItem(item: RecurringItem): Observable<RecurringItem> {
    return this.http.post<RecurringItem>(this.apiUrl, item);
  }

  updateRecurringItem(id: number, item: RecurringItem): Observable<RecurringItem> {
    return this.http.put<RecurringItem>(`${this.apiUrl}/${id}`, item);
  }

  deleteRecurringItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
