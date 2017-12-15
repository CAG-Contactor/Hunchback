import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GameTrackComponent } from './game-track.component';

describe('GameTrackComponent', () => {
  let component: GameTrackComponent;
  let fixture: ComponentFixture<GameTrackComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GameTrackComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameTrackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
