import { MotherOfAllGameStates } from './mother-of-all-game-states';

export interface Message {
  messageType: string;

  [propName: string]: any;
}

export type MessageUnion = Message & MotherOfAllGameStates | Message & Position;
