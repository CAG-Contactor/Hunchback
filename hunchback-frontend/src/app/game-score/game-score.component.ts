import {
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import * as _ from 'lodash';
import {BackendService} from '../backend.service';
import {ScoreCard} from '../game-model/score-card';

@Component({
  selector: 'app-game-score',
  templateUrl: './game-score.component.html',
  styleUrls: ['./game-score.component.scss']
})
export class GameScoreComponent implements OnInit {
  @Input()
  points: number;
  @ViewChild('nameInput')
  nameInput: ElementRef;

  highScores: ScoreCard[] = [];
  scoreCard: ScoreCard;
  scoreRegistered: boolean = false;

  constructor(private readonly backendService: BackendService) {
    this.initHighScoreList();
  }

  newScoreCard() {
    this.scoreCard = {
      userName: '',
      score: `${this.points}`
    };
    setTimeout(() => {
      this.nameInput.nativeElement.focus(), 100;
    });
  }

  sendScoreCard() {
    this.backendService
      .addScore(this.scoreCard)
      .subscribe(res => {
          this.scoreCard = undefined;
          this.backendService.getHighScores()
            .subscribe(m => {
              if (m.messageType === 'HighScores') {
                this.initHighScoreList(m);
                this.scoreRegistered = true;
              }
            });
        },
        err => {
          console.error(err);
        }
      );

  }

  ngOnInit(): void {
    this.backendService.getHighScores()
      .subscribe(m => {
        if (m.messageType === 'HighScores') {
          this.initHighScoreList(m);
        }
      });
  }

  private initHighScoreList(m?: any) {
    this.highScores = m && m.highScores || [];
    _.times(9 - this.highScores.length, () => this.highScores.push({userName: '---', score: '---'}));
    this.highScores = this.highScores.slice(0, 9);
  }

}
