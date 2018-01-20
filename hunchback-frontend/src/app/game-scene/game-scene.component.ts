import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { BackendService } from "../backend.service";
import { Subscription } from "rxjs/Subscription";
import { Gubbe } from "../game-model/gubbe";
import { Scene } from "../game-model/scene";

@Component({
  selector: 'app-game-scene',
  templateUrl: './game-scene.component.html',
  styleUrls: ['./game-scene.component.css']
})
export class GameSceneComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('canvas')
  canvasRef: ElementRef;
  private subscription: Subscription;
  private scene: Scene;
  private gubbe: Gubbe;

  constructor(private readonly backendService: BackendService) {
  }

  ngOnInit() {
    this.subscription = this.backendService.messageObservable()
      .subscribe(m => {
        if (m.messageType === 'Position') {
          this.updateView(m.position.x, m.position.y);
        }
      });
      const canvas = this.canvasRef.nativeElement as HTMLCanvasElement;
      this.scene = new Scene(canvas.getContext("2d"), 500, 500);
      this.gubbe = new Gubbe(0);
  }


  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  updateView(x: number, y: number) {
    console.debug('updateView:', x, y);
    this.scene.clear();
    // Update game state
    this.gubbe.moveTo(x, y);
    // Render
    this.gubbe.renderOn(this.scene);
  }

  ngAfterViewInit(): void {
    this.updateView(0, 0)
  }


}
