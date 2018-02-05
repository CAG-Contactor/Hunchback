import * as _ from 'lodash';
import { Actor } from './actor';
import { Scene } from './scene';
import { Shape } from './shape';

export class WindIndicator implements Shape, Actor {
  private static readonly sprites: { [direction: string]: HTMLImageElement } = {
    'S': new Image(),
    'SW': new Image(),
    'W': new Image(),
    'NW': new Image(),
    'N': new Image(),
    'NE': new Image(),
    'E': new Image(),
    'SE': new Image()
  };

  size: number;
  private _x: number;
  private _y: number;

  private windDirection: string;


  constructor(x: number, y: number) {
    this.size = 128;
    this._x = x;
    this._y = y;
    _.forIn(WindIndicator.sprites, (image, dir) => image.src = `/assets/compass/${dir}.png`);
  }

  get x(): number {
    return this._x;
  }

  get y(): number {
    return this._y;
  }

  renderOn(scene: Scene) {
    scene.drawTile(WindIndicator.sprites[this.windDirection], this.size, 0, 0, this.x, this.y);
  }

  moveTo(x: number, y: number) {
  }

  setWindDirection(windDirection: string) {
    this.windDirection = windDirection;
  }
}
