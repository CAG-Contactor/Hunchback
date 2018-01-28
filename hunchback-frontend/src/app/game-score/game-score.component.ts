import {Component} from '@angular/core';
import {BackendService} from "../backend.service";

export interface Score {
  nick: string,
  score: number
}
@Component({
  selector: 'app-game-score',
  templateUrl: './game-score.component.html',
  styleUrls: ['./game-score.component.css']
})
export class GameScoreComponent {

  constructor(private readonly backendService: BackendService) {
  }

}
