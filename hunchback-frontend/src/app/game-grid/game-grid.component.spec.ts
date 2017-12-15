import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GameGridComponent } from './game-grid.component';

describe('GameGridComponent', () => {
  let component: GameGridComponent;
  let fixture: ComponentFixture<GameGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GameGridComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
