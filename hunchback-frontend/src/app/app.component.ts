import { Component } from '@angular/core';
import { BackendService } from "./backend.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'app';
  messages: any[] = [];

  constructor(private readonly backendService: BackendService) {

  }

  ngOnInit(): void {
    this.backendService.startWebSocket();

    this.backendService.messageObservable()
      .subscribe(m => this.messages.push(m))
  }

}
