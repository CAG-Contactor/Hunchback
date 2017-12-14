import {
  Inject,
  Injectable
} from '@angular/core';
import { DOCUMENT } from "@angular/common";
import { Subject } from "rxjs/Subject";
import { ReplaySubject } from "rxjs/ReplaySubject";

@Injectable()
export class BackendService {
  private messageSubject: Subject<Message> = new ReplaySubject(1);
  private _ws: WebSocket;
  private backendServer: string;

  constructor(@Inject(DOCUMENT) private readonly document: Document) {
    this.backendServer = this.document.location.href.split('/')[2].split(':')[0];
  }

  messageObservable() {
    return this.messageSubject.asObservable();
  }

  startWebSocket() {
    if (!(<any>window).WebSocket) {
      alert("WebSocket not supported by this browser");
    }

    this._ws = new WebSocket(`ws://${this.backendServer}:7890/hunchback`);
    this._ws.onmessage = (m) => this._onmessage(m);
    this._ws.onclose = (m) => this._onclose(m);
  }

  _onmessage(m) {
    if (m.data) {
      this.messageSubject.next(m.data);
    }
  }

  _onclose(m) {
    this._ws = null;
  }
}

export interface Message {
  type: string
}
