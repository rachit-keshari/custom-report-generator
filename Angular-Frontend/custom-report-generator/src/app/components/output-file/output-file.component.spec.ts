import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutputFileComponent } from './output-file.component';

describe('OutputFileComponent', () => {
  let component: OutputFileComponent;
  let fixture: ComponentFixture<OutputFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OutputFileComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OutputFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
