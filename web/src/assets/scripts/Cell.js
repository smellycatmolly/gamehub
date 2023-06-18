export class Cell {
    constructor(r, c) {
        this.r = r;
        this.c = c; // 传过来的行数列数
        this.x = c + 0.5; // Cell的中心坐标
        this.y = r + 0.5;
    }
}