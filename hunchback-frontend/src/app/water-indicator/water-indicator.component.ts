import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';
import {BackendService} from "../backend.service";
import {Subscription} from "rxjs/Subscription";

@Component({
  selector: 'app-water-indicator',
  templateUrl: './water-indicator.component.html',
  styleUrls: ['./water-indicator.component.css']
})
export class WaterIndicatorComponent implements OnInit, AfterViewInit, OnDestroy {

  private subscription: Subscription;
  waterCapacity: number;
  points: number;
  fillPercentage: number;
  isFull: boolean;

  constructor(private readonly backendService: BackendService) {
  }

  ngOnInit() {
    this.subscription = this.backendService.messageObservable()
      .subscribe(m => {
        if (m.messageType === 'points') {
          this.updateView(m.points);
        }
      });
    this.isFull = false;
    this.points = 1000;
    this.waterCapacity = 2500;
    this.fillPercentage = this.getFillPercentage();
  }

  updateView(level: number) {
    this.points = level;
    this.fillPercentage = this.getFillPercentage();
    this.isFull = this.fillPercentage === 100;
  }

  getFillPercentage(): number {
    if (this.points >= this.waterCapacity) {
      return 100;
    } else {
      return Math.round(this.points / this.waterCapacity * 100);
    }
  }

  ngAfterViewInit(): void {
    this.updateView(1000);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
