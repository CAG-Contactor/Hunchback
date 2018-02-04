import {Component, OnInit, Input} from '@angular/core';
import {BackendService} from "../backend.service";
import {Subscription} from "rxjs/Subscription";
import {ScoreCard} from "../game-model/score-card";
import {NgModel} from "@angular/forms";

// export interface ScoreCard {
//   messageType: string,
//   userName: string,
//   score: number
// }
//
// export interface HighScores {
//   messageType: string,
//   highScores: ScoreCard[]
//
// }

@Component({
  selector: 'app-game-score',
  templateUrl: './game-score.component.html',
  styleUrls: ['./game-score.component.css']
})
export class GameScoreComponent implements OnInit {
  subscription: Subscription;
  highScores: ScoreCard[];

  scoreCard: ScoreCard;

  constructor(private readonly backendService: BackendService) {
  }

  newScoreCard() {
    this.scoreCard = {
      messageType: "Score",
      userName: "",
      score: 0
    };
  }

  ngOnInit(): void {
    this.backendService.getHighScores()
      .subscribe(m => {
        if (m.messageType === 'HighScores') {
          this.highScores = m.highScores;
        }
      });
  }

}
