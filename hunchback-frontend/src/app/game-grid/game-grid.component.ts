import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import {BackendService} from "../backend.service";
import {Subscription} from "rxjs/Subscription";
import {Gubbe} from "../game-model/gubbe";
import {Scene} from "../game-model/scene";
import {GridMap} from "../game-model/gridmap";

@Component({
  selector: 'app-game-grid',
  templateUrl: './game-grid.component.html',
  styleUrls: ['./game-grid.component.scss']
})
export class GameGridComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('bgCanvas') canvasBgRef: ElementRef;
  @ViewChild('iaCanvas') canvasIaRef: ElementRef;

  private subscription: Subscription;
  private bgScene: Scene; // Background scene
  private iaScene: Scene; // Interactive scene
  private gubbe: Gubbe;
  private gridMap: GridMap;
  private tileSetImage: HTMLImageElement;

  constructor(private readonly backendService: BackendService) {
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  paintMap(): void {
    this.bgScene.clear();
    const rows = this.gridMap.map.length;
    const cols = this.gridMap.map[0].length;
    const tileSize = this.gridMap.tileSize;
    console.log("Painting map: " + rows + " rows, " + cols + " columns");

    this.gridMap.map.forEach((row, y) => {
      row.forEach((tile, x) => {
        const x_pos = x * tileSize;
        const y_pos = y * tileSize;
        const tile_x_pos = tile * tileSize; // X position in tile-set image
        const tile_y_pos = 0; // Single-row sprite, always y-pos 0

        this.bgScene.drawTile(this.tileSetImage, tileSize, tile_x_pos, tile_y_pos, x_pos, y_pos);
      });
    });
  }

  updateView(x: number, y: number) {
    // Clear scene
    this.iaScene.clear();
    // Update game state
    this.gubbe.moveTo(x, y);
    // Render
    this.gubbe.renderOn(this.iaScene);
  }

  resizeCanvas(): void {
    const bgCanvas = this.canvasBgRef.nativeElement as HTMLCanvasElement;
    const iaCanvas = this.canvasIaRef.nativeElement as HTMLCanvasElement;

    bgCanvas.width = this.gridMap.width;
    bgCanvas.height = this.gridMap.height;
    iaCanvas.width = this.gridMap.width;
    iaCanvas.height = this.gridMap.height;
  }

  ngAfterViewInit(): void {
    this.backendService.getMap().subscribe(mapData => {
      this.gridMap = new GridMap(mapData);
      const bgCanvas = this.canvasBgRef.nativeElement as HTMLCanvasElement;
      const iaCanvas = this.canvasIaRef.nativeElement as HTMLCanvasElement;
      this.bgScene = new Scene(bgCanvas.getContext('2d'), this.gridMap.height, this.gridMap.width);
      this.iaScene = new Scene(iaCanvas.getContext('2d'), this.gridMap.height, this.gridMap.width);
      this.gubbe = new Gubbe(this.gridMap.tileSize);
      this.subscribeToUpdates();
      this.resizeCanvas();
      this.tileSetImage = new Image();
      this.tileSetImage.src = '/assets/tile-sets/tile-set-v2.png';
      this.tileSetImage.onload = () => {
        this.paintMap();
      };
    });
  }

  subscribeToUpdates(): void {
    this.subscription = this.backendService.messageObservable()
      .subscribe(m => {
        if (m.messageType === 'Position') {
          this.updateView(m.position.x, m.position.y);
        }
      });
  }
}
