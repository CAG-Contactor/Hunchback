import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { BackendService } from '../backend.service';
import { MotherOfAllGameStates } from '../game-model/mother-of-all-game-states';
import { GridMap } from '../game-model/shapes/gridmap';
import { Gubbe } from '../game-model/shapes/gubbe';
import { PointIndicators } from '../game-model/shapes/point-indicators';
import { Scene } from '../game-model/shapes/scene';
import { WindIndicator } from '../game-model/shapes/wind-indicator';

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
  private windIndicator: WindIndicator;
  private gridMap: GridMap;
  private tileSetImage: HTMLImageElement;
  private pointIndicators: PointIndicators;

  constructor(private readonly backendService: BackendService) {
  }

  ngOnInit() {
    this.gridMap = new GridMap(32);
    const bgCanvas = this.canvasBgRef.nativeElement as HTMLCanvasElement;
    const iaCanvas = this.canvasIaRef.nativeElement as HTMLCanvasElement;
    this.bgScene = new Scene(bgCanvas.getContext('2d'), this.gridMap.height, this.gridMap.width);
    this.iaScene = new Scene(iaCanvas.getContext('2d'), this.gridMap.height, this.gridMap.width);
    this.gubbe = new Gubbe(this.gridMap.tileSize);
    this.pointIndicators = new PointIndicators();
    this.windIndicator = new WindIndicator(370, 570);
    this.subscribeToUpdates();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  paintMap(): void {
    this.bgScene.clear();
    const rows = this.gridMap.map.length;
    const cols = this.gridMap.map[0].length;
    const tileSize = this.gridMap.tileSize;
    console.log('Painting map: ' + rows + ' rows, ' + cols + ' columns');

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

  renderView() {
    this.iaScene.clear();
    this.pointIndicators.renderOn(this.iaScene);
    this.gubbe.renderOn(this.iaScene);
    this.windIndicator.renderOn(this.iaScene);
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
          const moags = m as MotherOfAllGameStates;

          // Update game state
          this.windIndicator.setWindDirection(moags.wind.windDirection);
          this.gubbe.moveTo(moags.position.x, moags.position.y);
          this.pointIndicators.update(moags.gameState.pointIndicators);
          // Render view
          this.renderView();
        }
      });
  }
}
