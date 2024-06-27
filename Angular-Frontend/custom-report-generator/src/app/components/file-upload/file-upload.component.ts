import { SharedService } from './../../service/shared.service';
import { Component, OnInit } from '@angular/core';
import { HttpService } from 'src/app/service/http.service';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

  extension: string = "";
  randomNum: string = "";
  configUploadStatus: boolean = false;
  selectedFile: File | null = null;
  extensionMismatch: boolean = false;

  constructor(private http: HttpService, private shared: SharedService){}

  ngOnInit(): void {
      this.shared.configUploadStatus$.subscribe((status) => {
            this.configUploadStatus = status
      });
      this.shared.extension$.subscribe((ext) => {
        this.extension = ext
      });
      this.shared.randomNum$.subscribe((num) => {
        this.randomNum = num
      });
      console.log("Inside ngOnInit");
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onUpload() {
    if (this.selectedFile) {
      const fileExtension = this.selectedFile.name.split('.').pop()?.toLowerCase();
      if (fileExtension === this.extension) {
        this.extensionMismatch = false;
        const fileName = `input-${this.randomNum}.${this.extension}`;
        this.http.uploadFile(fileName, this.selectedFile).subscribe(
        (response) => {
            this.shared.setInputUploadStatus(true);
            console.log('File uploaded successfully');
          },
          (error) => {
            this.shared.setInputUploadStatus(false);
            console.error('Error uploading file:', error);
          }
        );
      } else {
        this.extensionMismatch = true;
        console.error('Selected file extension does not match the required extension');
      }
    } else {
      console.error('No file selected');
    }
  }
}
