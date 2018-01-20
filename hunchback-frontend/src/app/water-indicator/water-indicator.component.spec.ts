import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterIndicatorComponent } from './water-indicator.component';

describe('WaterIndicatorComponent', () => {
  let component: WaterIndicatorComponent;
  let fixture: ComponentFixture<WaterIndicatorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WaterIndicatorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WaterIndicatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
