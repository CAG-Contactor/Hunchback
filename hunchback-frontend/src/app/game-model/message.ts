import { MotherOfAllGameStates } from './mother-of-all-game-states';
import {ScoreCard} from "./score-card";

export interface Message {
  messageType: string;

  [propName: string]: any;
}

export type MessageUnion = Message & MotherOfAllGameStates | Message & Position;
