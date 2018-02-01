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
        if (m.messageType === 'points') this.points = m.points;
      });
  }

}
