import {Shape} from "./shape";
import {Actor} from "./actor";
import {Scene} from "./scene";

export class Gubbe implements Shape, Actor {

  private _x: number;
  private _y: number;
  private sprite;

  readonly size: number;

  constructor(size: number) {
    this.size = size;
    this.sprite = new Image();
    this.sprite.src = '/assets/character_1.png';
  }

  get x(): number {
    return this._x;
  }

  get y(): number {
    return this._y;
  }

  renderOn(scene: Scene) {
    scene.drawTile(this.sprite, this.size, 0, 0, this._x, this._y);
  }

  moveTo(x: number, y: number) {
    this._x = x;
    this._y = y;
  }
}
