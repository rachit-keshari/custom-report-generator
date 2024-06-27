import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { SharedService } from './shared.service';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  private apiUrl = 'https://xyzxyzxyz.execute-api.ap-south-1.amazonaws.com/prod'; // Replace with your API base URL

  constructor(private http: HttpClient, private shared: SharedService) { }

  //Checks if File Exists:
  fileExist(fileName: string): Observable<{ exist: string }> {
    const url = `${this.apiUrl}/file-exist/${fileName}`;
    return this.http.get<{ exist: string }>(url)
      .pipe(
        catchError(this.handleError)
      );
  }

  // GET request to download a CSV file
  downloadFile(fileName: string): Observable<Blob> {
    const url = `${this.apiUrl}/download/${fileName}`;
    return this.http.get(url, { responseType: 'blob' })
      .pipe(
        catchError(this.handleError)
      );
  }

  // POST request to upload a JSON configuration
  uploadConfig(config: any): Observable<any> {
    const url = `${this.apiUrl}/upload/config`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(url, config, { headers })
      .pipe(
        tap(() => {
          this.shared.setConfigUploadStatus(true);
          this.shared.setRandomNum(config.randomNo);
          this.shared.setExtension(config.extension);
         }),
        catchError(error => {
          this.shared.setConfigUploadStatus(false);
          return this.handleError(error);
        })
      );
  }

  // POST request to upload a file
  uploadFile(fileName: string, file: File): Observable<any> {
    const url = `${this.apiUrl}/upload/input/${fileName}`;
    const headers = new HttpHeaders();

    const formData = new FormData();
    formData.append('file', file, file.name);

    return new Observable(observer => {
      const reader = new FileReader();

      reader.onload = (event: ProgressEvent<FileReader>) => {
        const csvContent = event.target!.result as string;

        // Extract CSV content from reader result and send as body
        this.http.post(url, csvContent, { headers }).subscribe(
          response => {
            observer.next(response);
            observer.complete();
          },
          error => observer.error(error)
        );
      };
      reader.readAsText(file);
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError(error);
  }

}
