export class GridMap {
  private _tileSize: number;
  private _rows: number;
  private _cols: number;
  private _array: number[][];

  constructor(mapData) {
    this._tileSize = mapData.tileSize;
    this._array = mapData.map;
    this._rows = mapData.nrOfRows;
    this._cols = mapData.nrOfColumns;
  }

  get map(): number[][] {
    return this._array;
  }

  set map(value: number[][]) {
    this._array = value;
  }

  get cols(): number {
    return this._cols;
  }

  get rows(): number {
    return this._rows;
  }

  get tileSize(): number {
    return this._tileSize;
  }

  get height(): number {
    return this.tileSize * this.rows;
  }

  get width(): number {
    return this.tileSize * this.cols;
  }

}
