import {
  Component,
  Inject
} from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'app';
  messages: string[] = [];
  private _ws: WebSocket;

  constructor(@Inject(DOCUMENT) private readonly document: Document) {

  }

  ngOnInit(): void {
    const backendServer = this.document.location.href.split('/')[2].split(':')[0];
    if (!(<any>window).WebSocket) {
      alert("WebSocket not supported by this browser");
    }

    this._ws = new WebSocket(`ws://${backendServer}:7890/camel-iss`);
    this._ws.onmessage = (m) => this._onmessage(m);
    this._ws.onclose = (m) => this._onclose(m);
  }

  _onmessage(m) {
    if (m.data) {
      this.messages.push(m.data);
    }
  }

  _onclose(m) {
    this._ws = null;
  }

}
