import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FreezerItem {
  id?: number;
  name: string;
  quantity: number;
  isFullMeal: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FreezerService {
  private apiUrl = '/api/freezer';

  constructor(private http: HttpClient) {}

  getAllItems(): Observable<FreezerItem[]> {
    return this.http.get<FreezerItem[]>(this.apiUrl);
  }

  addItem(item: FreezerItem): Observable<FreezerItem> {
    return this.http.post<FreezerItem>(this.apiUrl, item);
  }

  deleteItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}