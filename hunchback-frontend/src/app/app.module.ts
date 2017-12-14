import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';


import { AppComponent } from './app.component';
import { BackendService } from "./backend.service";
import { GameGridComponent } from './game-grid/game-grid.component';


@NgModule({
  declarations: [
    AppComponent,
    GameGridComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [BackendService],
  bootstrap: [AppComponent]
})
export class AppModule { }
