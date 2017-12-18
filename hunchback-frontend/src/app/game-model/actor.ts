import { Scene } from "./scene";

export interface Actor {
  moveTo(x: number, y: number);
  renderOn(scene: Scene);
}
