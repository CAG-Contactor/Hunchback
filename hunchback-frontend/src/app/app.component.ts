import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'app';
  messages: string[] = [];
  private _ws: WebSocket;

  ngOnInit(): void {

    if (!(<any>window).WebSocket) {
      alert("WebSocket not supported by this browser");
    }

    this._ws = new WebSocket("ws://localhost:7890/camel-iss");
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
