import {
  Component,
  HostListener,
  OnInit
} from '@angular/core';
import { BackendService } from './backend.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'app';
  messages: any[] = [];
  position = [0, 0];
  status: string;
  points: number;

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
        if (m.messageType === 'Position') this.position = [m.position.x, m.position.y];
        if (m.messageType === 'points') this.points = m.points;
      });
  }

}
