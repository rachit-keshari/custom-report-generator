import { SharedService } from './../../service/shared.service';
import { HttpService } from 'src/app/service/http.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-output-file',
  templateUrl: './output-file.component.html',
  styleUrls: ['./output-file.component.css']
})
export class OutputFileComponent implements OnInit {
  loading: boolean = true;
  randomNum: string = "";
  inputUploadStatus: boolean = false;
  outputFileName: string = "";

  constructor(private shared: SharedService, private http: HttpService) {}

  ngOnInit(): void {
    this.shared.randomNum$.subscribe((num) => {
      this.randomNum = num;
    });
    this.shared.inputUploadStatus$.subscribe((status) => {
      this.inputUploadStatus = status;
      if (status) {
        this.outputFileName = `report-${this.randomNum}.csv`;
        this.checkFileExistence();
      }
    });
  }

  checkFileExistence(retries: number = 20, delay: number = 5000): void {
    const checkFile = () => {
      this.http.fileExist(this.outputFileName).subscribe(response => {
        console.log("file-exist check retry no: "+retries);
        if (response.exist === "true") {
          this.loading = false;
        } else if (retries > 0) {
          setTimeout(() => {
            checkFile();
          }, delay);
        } else {
          this.loading = false;
          console.error('File does not exist after maximum retries');
        }
      }, error => {
        this.loading = false;
        console.error('Error checking file existence:', error);
      });
    };
    checkFile();
  }

  downloadFile(): void {
    this.http.downloadFile(this.outputFileName).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = this.outputFileName;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    }, error => {
      console.error('Error downloading file:', error);
    });
  }

  openInNewTab(): void {
    this.http.downloadFile(this.outputFileName).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    }, error => {
      console.error('Error opening file in new tab:', error);
    });
  }
}
