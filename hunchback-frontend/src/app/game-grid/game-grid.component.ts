import { Component, OnInit } from '@angular/core';
import {Sprite} from "./sprite";
import { BackendService } from "../backend.service";

@Component({
  selector: 'app-game-grid',
  templateUrl: './game-grid.component.html',
  styleUrls: ['./game-grid.component.css']
})
export class GameGridComponent implements OnInit {

  sprites : Sprite[];

  constructor(private readonly backendService: BackendService) {
    this.clearGrid();
  }

  ngOnInit() {
    this.backendService.messageObservable()
      .subscribe(m => {
        if (m.messageType === 'Position') this.updateGrid(m.position.x, m.position.y)
      })

  }

  private clearGrid() {
    this.sprites = [];
    for (let i = 0; i < 20*20; i++) {
      this.sprites.push(new Sprite(''));
    }
  }

  private updateGrid(x: number, y:number) {
    x = x < 20 ? x : 19;
    y = y < 20 ? y : 19;
    this.clearGrid();
    this.sprites[x + 20*(19 - y)] = new Sprite('*');
  }
}
