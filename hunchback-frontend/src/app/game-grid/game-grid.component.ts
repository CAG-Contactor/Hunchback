import { Component, OnInit } from '@angular/core';
import {Sprite} from "./sprite";

@Component({
  selector: 'app-game-grid',
  templateUrl: './game-grid.component.html',
  styleUrls: ['./game-grid.component.css']
})
export class GameGridComponent implements OnInit {

  sprites : Sprite[];

  constructor() {
    this.sprites = GameGridComponent.mockGridContents();
  }

  static mockGridContents(): any {
    let sprites = [];
    for (let i = 0; i < 100; i++) {
      let sprite = new Sprite();
      sprite.position = i;
      sprites.push(new Sprite());
    }
    return sprites;
  }

  ngOnInit() {
  }

}
