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
  waterLevel: number;
  fillPercentage: number;
  isFull: boolean;

  constructor(private readonly backendService: BackendService) {
  }

  ngOnInit() {
    this.subscription = this.backendService.messageObservable()
      .subscribe(m => {
        if (m.messageType === 'waterlevel') {
          this.updateView(m.level);
        }
      });
    this.isFull = false;
    this.waterLevel = 1000;
    this.waterCapacity = 2500;
    this.fillPercentage = this.getFillPercentage();
  }

  updateView(level: number) {
    this.waterLevel = level;
    this.fillPercentage = this.getFillPercentage();
    this.isFull = this.fillPercentage === 100;
  }

  getFillPercentage(): number {
    if (this.waterLevel >= this.waterCapacity) {
      return 100;
    } else {
      return Math.round(this.waterLevel / this.waterCapacity * 100);
    }
  }

  ngAfterViewInit(): void {
    this.updateView(1000);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
