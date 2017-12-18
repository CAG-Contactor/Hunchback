import { Shape } from "./shape";
import { Actor } from "./actor";
import { Scene } from "./scene";

export class Gubbe implements Shape, Actor {
  private _x: number;
  get x(): number {
    return this._x;
  }
  private _y: number;
  get y(): number {
    return this._y;
  }
  readonly height: number = 10;
  readonly width: number = 10;

  renderOn(scene: Scene) {
    scene.transformPosAndDraw(this, (x, y, c2d) => {
      const oldFillStyle = c2d.fillStyle;
      c2d.fillStyle = 'yellow';
      c2d.fillRect(x, y, this.height, this.width);
      c2d.fillStyle = oldFillStyle;
    });
  }

  moveTo(x: number, y: number) {
    this._x = x;
    this._y = y;
  }
}
