import { Shape } from "./shape";

export class Scene {
  constructor(private readonly renderingContext: CanvasRenderingContext2D,
              private readonly height: number,
              private readonly width: number) {

  }

  transformPosAndDraw(shape: Shape, drawOperation: (x: number, y: number, tdc: CanvasRenderingContext2D) => void) {
    drawOperation(
      Math.max(0, Math.min(shape.x, this.width - shape.size)),
      Math.max(0, this.height - shape.size - Math.min(shape.y, this.height - shape.size)),
      this.renderingContext
    );
  }

  draw(drawOperation: (tdc: CanvasRenderingContext2D) => void) {
    const oldFillStyle = this.renderingContext.fillStyle;
    drawOperation(
      this.renderingContext
    );
    this.renderingContext.fillStyle = oldFillStyle;
  }

  drawTile(tileSetImage, tileSize, tile_x_pos, tile_y_pos, x_pos, y_pos) {
    this.renderingContext.drawImage(
      tileSetImage,
      tile_x_pos,
      tile_y_pos,
      tileSize,
      tileSize,
      x_pos,
      y_pos,
      tileSize,
      tileSize);
  }

  clear() {
    this.renderingContext.clearRect(0, 0, this.width, this.height)
  }

}
