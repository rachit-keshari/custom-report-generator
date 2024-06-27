import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private configUploadStatus = new BehaviorSubject<boolean>(false);
  configUploadStatus$ = this.configUploadStatus.asObservable();

  private randomNum = new BehaviorSubject<string>("");
  randomNum$ = this.randomNum.asObservable();

  private extension = new BehaviorSubject<string>("");
  extension$ = this.extension.asObservable();

  private inputUploadStatus = new BehaviorSubject<boolean>(false);
  inputUploadStatus$ = this.inputUploadStatus.asObservable();

  setInputUploadStatus(status: boolean): void {
      this.inputUploadStatus.next(status);
  }

  setExtension(ext: string): void {
    this.extension.next(ext);
  }

  setRandomNum(num: string): void {
   this.randomNum.next(num);
  }

  setConfigUploadStatus(status: boolean): void {
    this.configUploadStatus.next(status);
  }
}
