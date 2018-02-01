import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
  FusionChartsModule
} from 'angular4-fusioncharts';
import * as FusionCharts from 'fusioncharts';
import * as Charts from 'fusioncharts/fusioncharts.charts';
import * as FintTheme from 'fusioncharts/themes/fusioncharts.theme.fint';

import { AppComponent } from './app.component';
import { BackendService } from './backend.service';
import { GameGridComponent } from './game-grid/game-grid.component';
import { GameSceneComponent } from './game-scene/game-scene.component';
import { GameScoreComponent } from './game-score/game-score.component';
import { WaterIndicatorComponent } from './water-indicator/water-indicator.component';


@NgModule({
  declarations: [
    AppComponent,
    GameGridComponent,
    GameSceneComponent,
    GameScoreComponent,
    WaterIndicatorComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FusionChartsModule.forRoot(FusionCharts, Charts, FintTheme)
  ],
  providers: [BackendService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
