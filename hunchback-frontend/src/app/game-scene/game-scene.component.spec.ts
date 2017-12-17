import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GameSceneComponent } from './game-cene.component';

describe('GameSceneComponent', () => {
  let component: GameSceneComponent;
  let fixture: ComponentFixture<GameTrackComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GameSceneComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameSceneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
