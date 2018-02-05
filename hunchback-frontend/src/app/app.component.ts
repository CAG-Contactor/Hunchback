import {
  Component,
  HostListener,
  OnInit
} from '@angular/core';
import { BackendService } from './backend.service';
import { MotherOfAllGameStates } from './game-model/mother-of-all-game-states';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  message: string;
  secondsLeft: number;
  position = [0, 0];
  status: string;
  points: number = 0;
  chart = chartDef;

  constructor(private readonly backendService: BackendService) {
  }

  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    switch (event.key) {
      case 'w':
      case 'W':
        this.backendService.sendUp();
        break;
      case 'a':
      case 'A':
        this.backendService.sendLeft();
        break;
      case 's':
      case 'S':
        this.backendService.sendDown();
        break;
      case 'd':
      case 'D':
        this.backendService.sendRight();
        break;
    }
  }

  restartGame(): void {
    this.backendService.restartGame();
  }

  ngOnInit(): void {
    this.backendService.startWebSocket();
    this.backendService.webSocketStatus()
      .subscribe(sts => this.status = sts);
    this.backendService.messageObservable()
      .subscribe(m => {
        if (m.messageType === 'Position') {
          const moags: MotherOfAllGameStates = m as MotherOfAllGameStates;
          this.points = moags.gameState.points;
          this.secondsLeft = moags.gameState.time;
          switch(moags.gameState.state) {
            case 'FINISHED':
              this.message = 'GAME OVER';
              break;
            case 'ARMED':
              this.message = 'READY PLAYER ONE';
              break;
            default:
              this.message = undefined;
          }
        }
      });
  }

}

const chartDef = {
  "chart": {
    "caption": "Customer Satisfaction Score",
    "subcaption": "Last week",
    "gaugeStartAngle": "0",
    "gaugeEndAngle": "90",
    "lowerLimit": "0",
    "upperLimit": "100",
    "gaugeFillMix": "{dark-40},{light-40},{dark-20}",
    "theme": "fint"
  },
  "colorRange": {
    "color": [
      {
        "minValue": "0",
        "maxValue": "50",
        "code": "#e44a00"
      },
      {
        "minValue": "50",
        "maxValue": "75",
        "code": "#f8bd19"
      },
      {
        "minValue": "75",
        "maxValue": "100",
        "code": "#6baa01"
      }
    ]
  },
  "dials": {
    "dial": [
      {
        "value": "67",
        "radius": "140",
        "rearExtension": "15"
      }
    ]
  }
}
