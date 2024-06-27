import { Component } from '@angular/core';
import { HttpService } from '../../service/http.service';

@Component({
  selector: 'app-config-json-build',
  templateUrl: './config-json-build.component.html',
  styleUrls: ['./config-json-build.component.css']
})
export class ConfigJsonBuildComponent {

  jsonConfig: string = "";
  objIndex: number = 0;
  indexExist: boolean = true;
  configUploaded: boolean = false;
  uploadStatus: string = "";
  randomNum: string = "";

  constructor(private httpService: HttpService){
      let configStr: string = JSON.stringify({"input_file_extension":null,"output_file_headers":[],"output_file_config":[]});
      this.jsonConfig = JSON.stringify(JSON.parse(configStr),null,2);
  }

  addExtension(template: string) {
    if (template === 'csv') {
          this.updateConfig('input_file_extension','csv');
    }else if(template === 'xlsx') {
          this.updateConfig('input_file_extension','xlsx');
    }else if(template === 'reset') {
      this.updateConfig('input_file_extension',null);
    }
  }

  addHeaders(template: string) {
    let config = JSON.parse(this.jsonConfig);
    if (template === 'example') {
          config.output_file_headers = JSON.parse(JSON.stringify(["Amount","UTR","CustomerName","PhoneNo"]));
    }else if(template === 'empty') {
      config.output_file_headers = JSON.parse(JSON.stringify(["","","",""]));
    }else if(template === 'reset') {
      config.output_file_headers = JSON.parse(JSON.stringify([]));
    }
    this.jsonConfig = JSON.stringify(config,null,2);
  }

  addTransform(template: string){
    let config = JSON.parse(this.jsonConfig);
    if (template === 'rename') {
          config.output_file_config.push({name:"",rename_to:""});
    }else if(template === 'transform') {
            config.output_file_config.push({name:"",transform:""});
    }else if(template === 'both') {
      config.output_file_config.push({name:"",rename_to:"",transform:""});
    }else if(template === 'example') {
      config.output_file_config = JSON.parse(JSON.stringify([{"name":"amount","rename_to":"Amount","transform":"ConvertToRupee"},{"name":"utr","rename_to":"UTR","transform":"RemoveFromStart:XDS35|AddLeadingZeros:18"},{"name":"ph.no","rename_to":"PhoneNo","transform":"LimitLengthFromBack:10|AddPrefix:+91 "},{"name":"name","rename_to":"CustomerName"}]));
    }else if(template === 'reset') {
      config.output_file_config = JSON.parse(JSON.stringify([]));
    }
    this.jsonConfig = JSON.stringify(config,null,2);
  }

  appendTransform(template: string){
    if(this.objIndex < 0) return;
    let config = JSON.parse(this.jsonConfig);
    if(config.output_file_config && config.output_file_config[this.objIndex]){
      this.indexExist = true;

      if(template === 'removeAll'){
        config.output_file_config[this.objIndex].transform = "";
        this.jsonConfig = JSON.stringify(config, null, 2);
        return;
      }else if(template === 'removeTransform'){
        let temp = config.output_file_config[this.objIndex];
        delete temp.transform;
        config.output_file_config[this.objIndex]=temp;
        this.jsonConfig = JSON.stringify(config, null, 2);
        return;
      }

      let temp = config.output_file_config[this.objIndex].transform || "";
      config.output_file_config[this.objIndex].transform = `${temp===""?"":temp+"|"}${template}`;
      this.jsonConfig = JSON.stringify(config, null, 2);
    } else {
       this.indexExist = false;
    }
  }

  updateConfig(key: string, value: any): void {
    let config = JSON.parse(this.jsonConfig);
    config[key] = value;
    this.jsonConfig = JSON.stringify(config, null, 2);
  }

  uploadConfig() {
    // Implement the logic to upload the final config
    try {
      const configObject = JSON.parse(this.jsonConfig);
      const randomNo = Math.floor(100000000 + Math.random() * 900000000).toString().substring(0, 9); // Generate 9-digit random number
      this.randomNum = randomNo;
      if(configObject.input_file_extension==null){
         this.uploadStatus = "input_file_extension can't be null";
         return;
      }

      const requestBody = {
        extension: configObject.input_file_extension,
        randomNo: randomNo,
        fileContent: JSON.stringify(configObject,null,2)
      };

      // Make API call with requestBody
      this.postDataToApi(requestBody);
    } catch (error) {
      console.error('Error parsing jsonConfig:', error);
      this.uploadStatus = 'Error parsing JSON configuration.';
    }
  }

   // Method to post data to API
   private postDataToApi(requestBody: any) {
    this.httpService.uploadConfig(requestBody).subscribe(
      (response) => {
            console.log('API response',response);
            this.configUploaded = true;
            this.uploadStatus = 'Config uploaded successfully.';
      },
      (error) => {
            console.error('Error calling API:', error);
            this.configUploaded = false;
            this.uploadStatus = 'Failed to upload config.';
      }
    );
  }

}
