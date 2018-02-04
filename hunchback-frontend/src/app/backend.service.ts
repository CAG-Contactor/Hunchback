import { DOCUMENT } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
  Inject,
  Injectable
} from '@angular/core';
import 'rxjs/add/operator/take';
import 'rxjs/add/operator/take';
import { Observable } from 'rxjs/Observable';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { Subject } from 'rxjs/Subject';
import { MessageUnion } from './game-model/message';
import {ScoreCard} from "./game-model/score-card";
import {HighScoreList} from "./game-model/high-scores";


@Injectable()
export class BackendService {
  private messageSubject: Subject<MessageUnion> = new ReplaySubject(1);
  private statusSubject: Subject<string> = new ReplaySubject(1);
  private _ws: WebSocket;
  private backendServer: string;
  private port: number = 8080;

  constructor(@Inject(DOCUMENT) private readonly document: Document, private readonly httpClient: HttpClient) {
    this.statusSubject.next('Disconnected');
    this.backendServer = this.document.location.href.split('/')[2].split(':')[0];
  }


  webSocketStatus(): Observable<string> {
    return this.statusSubject.asObservable();
  }

  messageObservable() {
    return this.messageSubject.asObservable();
  }

  startWebSocket() {
    if (!(<any>window).WebSocket) {
      alert("WebSocket not supported by this browser");
    }

    this._ws = new WebSocket(`ws://${this.backendServer}:7890/hunchback`);
    this._ws.onopen = this.handleOpen;
    this._ws.onmessage = this.handleMessage;
    this._ws.onclose = this.handleClose;
    this._ws.onerror = this.handleError;
  }

  handleOpen = (o) => {
    console.info(o);
    this.statusSubject.next('Connected');
  };

  handleError = (e) => {
    console.error(e);
    this.statusSubject.next('Error');
  };

  handleMessage = (m) => {
    if (m.data) {
      console.debug('onmessage:', m.data);
      this.messageSubject.next(JSON.parse(m.data));
    }
  };

  handleClose = (m) => {
    console.warn(m);
    this._ws = null;
    this.statusSubject.next('Disconnected');
    setTimeout(() => this.startWebSocket(), 10000);
  };

  sendUp() {
    this.httpClient.get<MessageUnion>(`http://${this.backendServer}:${this.port}/direction/up`)
      .subscribe(r => console.debug(r), e => console.error(e));
  }

  sendLeft() {
    this.httpClient.get<MessageUnion>(`http://${this.backendServer}:${this.port}/direction/left`)
      .subscribe(r => console.debug(r), e => console.error(e));
  }

  sendDown() {
    this.httpClient.get<MessageUnion>(`http://${this.backendServer}:${this.port}/direction/down`)
      .subscribe(r => console.debug(r), e => console.error(e));
  }

  sendRight() {
    this.httpClient.get<MessageUnion>(`http://${this.backendServer}:${this.port}/direction/right`)
      .subscribe(r => console.debug(r), e => console.error(e));
  }

  restartGame() {
    this.httpClient.get<MessageUnion>(`http://${this.backendServer}:${this.port}/game/restart`)
      .subscribe(r => console.debug(r), e => console.error(e));
  }

  getMap() {
    return this.httpClient.get<any>(`http://${this.backendServer}:${this.port}/map`);
  }

  getHighScores() {
    return this.httpClient.get<HighScoreList>(`http://${this.backendServer}:${this.port}/scores`);

  }

  addScore(score: ScoreCard) {
    this.httpClient.post(`http://${this.backendServer}:${this.port}/score/add`, score)
  }


}

