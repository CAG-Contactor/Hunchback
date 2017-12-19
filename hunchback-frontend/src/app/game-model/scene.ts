import { Shape } from "./shape";

export class Scene {
  constructor(private readonly renderingContext: CanvasRenderingContext2D,
              private readonly height: number,
              private readonly width: number) {

  }

  transformPosAndDraw(shape: Shape, drawOperation: (x: number, y: number, tdc: CanvasRenderingContext2D) => void) {
    drawOperation(
      Math.max(0, Math.min(shape.x, this.width - shape.width)),
      Math.max(0, this.height - shape.width - Math.min(shape.y, this.height - shape.width)),
      this.renderingContext
    );
  }

  clear() {
    this.renderingContext.clearRect(0, 0, this.width, this.height)
  }

}
