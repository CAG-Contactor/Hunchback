import { PointIndicator } from './point-indicator';
import { Position } from './position';

export interface MotherOfAllGameStates {
  wind: {
    x: number;
    y: number;
  }
  inertia: {
    x: number;
    y: number;
  }
  position: Position;
  stepFrequency: number;
  gameState: {
    state: 'FINISHED' | 'ARMED' | 'RUNNING';
    time: number;
    pointIndicators: PointIndicator[];
  }
}
