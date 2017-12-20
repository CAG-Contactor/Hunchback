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
  styleUrls: ['./game-grid.component.css']
})
export class GameGridComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('canvas')
  canvasRef: ElementRef;
  private subscription: Subscription;
  private scene: Scene;
  private gubbe: Gubbe;
  private gridMap: GridMap;

  constructor(private readonly backendService: BackendService) {
  }

  ngOnInit() {
    this.subscribeToUpdates();
    this.gridMap = new GridMap(24, 20, 20);
    const canvas = this.canvasRef.nativeElement as HTMLCanvasElement;
    canvas.setAttribute("width", this.gridMap.width + 'px');
    canvas.setAttribute("height", this.gridMap.height + 'px');
    this.scene = new Scene(canvas.getContext('2d'), this.gridMap.height, this.gridMap.width);
    this.gubbe = new Gubbe();
    this.paintMap();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  paintMap(): void {
    this.scene.clear();
    const rows = this.gridMap.map.length;
    const cols = this.gridMap.map[0].length;
    console.log("Painting map: " + rows + " rows, " + cols + " columns");

    this.gridMap.map.forEach((row, y) => {
      row.forEach((tile, x) => {
        if (tile !== 0) { // tile is not walkable
          this.scene.draw((c2d) => {
            const x_pos = x * this.gridMap.tileSize;
            const y_pos = y * this.gridMap.tileSize;
            c2d.fillStyle = 'blue';
            c2d.fillRect(x_pos, y_pos, this.gridMap.tileSize, this.gridMap.tileSize);
          });
        }
      });
    });
  }

  updateView(x: number, y: number) {
    console.debug('updateView:', x, y);
    // Reset/clear previous position
    this.gubbe.removeFrom(this.scene);
    // Update game state
    this.gubbe.moveTo(x, y);
    // Render
    this.gubbe.renderOn(this.scene);
  }

  ngAfterViewInit(): void {
    //this.updateView(0, 0);
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
