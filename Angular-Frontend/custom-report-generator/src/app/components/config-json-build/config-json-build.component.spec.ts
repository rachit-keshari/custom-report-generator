import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigJsonBuildComponent } from './config-json-build.component';

describe('ConfigJsonBuildComponent', () => {
  let component: ConfigJsonBuildComponent;
  let fixture: ComponentFixture<ConfigJsonBuildComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConfigJsonBuildComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigJsonBuildComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
