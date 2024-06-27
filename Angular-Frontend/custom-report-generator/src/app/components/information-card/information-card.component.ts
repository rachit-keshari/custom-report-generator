import { Component } from '@angular/core';

@Component({
  selector: 'app-information-card',
  templateUrl: './information-card.component.html',
  styleUrls: ['./information-card.component.css']
})
export class InformationCardComponent {
  items = [
    { title: 'Custom-Report-Generator', description: 'It is a prototype working application, to convert large csv or xlsx file, to desired output file. it supports customized Headers & its customized order, with chain of transformation we want to do on particular input file columns.', imagePath: '../../../assets/chart1.JPG' },
    { title: 'Extension', description: 'Choose the extension of your input file, we currently support csv & xlsx format only.' },
    { title: 'Headers', description: 'Please add Headers which you want in output file, in the list output_file_headers: eg: "Account","Name", etc (headers order matters)' },
    { title: 'Rename', description: 'Please add object in output_file_config list: name define: input file column name, rename defines output file renamed value.' },
    { title: 'Transform', description: 'Please add object in output_file_config list: name define: input file column name, transform defines chain of transformantion which we want over inputFile data which takes input_param, each transform is separated by "|" pipe delimiter. eg: "RemoveFromStart:XDS35|AddLeadingZeros:18" output of first transformation acts as input for other transformation.' },
    { title: 'Example', description: 'choose Example for each to get a working configuration example.' },
    { title: 'Troubleshoot', description: 'If you are facing issue please write you query to: rachit.keshari@gmail.com, it\'s a prototype project, you can check-entire code over github, working demo over youtube' }
  ];

  activeItem: number | null = null;

  toggleCollapse(index: number): void {
    this.activeItem = this.activeItem === index ? null : index;
  }
}
