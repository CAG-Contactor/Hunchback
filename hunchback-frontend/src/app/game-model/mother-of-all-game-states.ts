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
    time: number;
    state: 'FINISHED' | 'ARMED' | 'RUNNING';
    pointIndicators: PointIndicator[];
  }
}
