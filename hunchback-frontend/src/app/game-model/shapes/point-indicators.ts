import { PointIndicator } from '../point-indicator';
import { Actor } from './actor';
import { Scene } from './scene';

export class PointIndicators implements Actor {
  private readonly minusSprite = new Image();
  private readonly plusSprite = new Image();
  private pointIndicators: PointIndicator[];

  update(pointIndicators: PointIndicator[]) {
    this.minusSprite.src = '/assets/pile-of-poo-32x32.png';
    this.plusSprite.src = '/assets/cake.png';
    this.pointIndicators = pointIndicators;
  }

  moveTo(x: number, y: number) {
    // NOP
  }

  renderOn(scene: Scene) {
    this.pointIndicators && this.pointIndicators.forEach(
      pi => {
        const sprite = pi.pointIndicatorType === 'MINUS' ?
          this.minusSprite :
          this.plusSprite;

        scene.drawTile(sprite, 32, 0, 0, pi.x, pi.y);
      }
    );
  }
}
