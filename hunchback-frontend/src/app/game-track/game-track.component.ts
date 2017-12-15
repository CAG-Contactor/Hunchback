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

@Component({
  selector: 'app-game-track',
  templateUrl: './game-track.component.html',
  styleUrls: ['./game-track.component.css']
})
export class GameTrackComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('canvas')
  canvasRef: ElementRef;
  private subscription: Subscription;

  constructor(private readonly backendService: BackendService) {
  }

  ngOnInit() {
    this.subscription = this.backendService.messageObservable()
      .subscribe(m => {
        if (m.messageType === 'Position') this.updateView(m.position.x, m.position.y)
      })
  }


  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  updateView(x: number, y: number) {
    console.debug('updateView:', x, y);
    const canvas = this.canvasRef.nativeElement as HTMLCanvasElement;
    const context2d = canvas.getContext("2d");
    context2d.clearRect(0, 0, 500, 500);
    context2d.fillStyle = 'yellow';
    context2d.fillRect(Math.min(x, 480), 480-Math.min(y, 480), 20, 20);
  }

  ngAfterViewInit(): void {
    this.updateView(0, 0)
  }


}
