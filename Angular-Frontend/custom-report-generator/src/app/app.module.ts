import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ConfigJsonBuildComponent } from './components/config-json-build/config-json-build.component';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { InformationCardComponent } from './components/information-card/information-card.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { OutputFileComponent } from './components/output-file/output-file.component';

@NgModule({
  declarations: [
    AppComponent,
    ConfigJsonBuildComponent,
    FileUploadComponent,
    InformationCardComponent,
    NavbarComponent,
    OutputFileComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
