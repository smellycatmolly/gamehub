import { GameObjects } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObjects {
    constructor(info, gamemap) {
        super();

        this.id = info.id; // 是哪条蛇
        this.color = info.color;
        this.gamemap = gamemap; // 取地图的引用，方便使用地图的性质

        this.cells = [new Cell(info.r, info.c)]; // 存放蛇的身体，cells[0]存放蛇头
        this.next_cell = null;   // 下一步的目标位置

        this.speed = 5; // 蛇每秒走5个格子
        this.direction = -1; // -1表示没有指令，0、1、2、3表示上右下左
        this.status = "idle"; // idle表示静止，move表示正在移动，die表示死亡

        this.dr = [-1, 0, 1, 0];   // 4个方向行的偏移量
        this.dc = [0, 1, 0, -1];   // 4个方向列的偏移量

        this.step = 0;  // 表示回合数
        this.eps = 1e-2;  // 允许的误差

        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2;  // 左下角的0号蛇默认方向向上，右上角1号蛇默认方向向下

        this.eye_dx = [  // 在不同方向时蛇眼睛的x偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [  // 在不同方向时蛇眼睛的y偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ]
    }

    start() {

    }

    set_direction(d) {
        this.direction = d;
    }

    check_tail_increasing() {  // 检测当前回合蛇的长度是否需要增加
        if (this.step <= 10) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }

    next_step() {   // 将蛇的状态变为走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;  // 清空操作
        this.status = "move";
        this.step ++ ;

        const k = this.cells.length;
        for (let i = k; i > 0; i -- ) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));  // 所有格子都往后挪一位，就会出现新的蛇头：123 -> 1123
        }
    }

    update_move() {
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);  // 随着快到目标点了就越来越小

        if (distance < this.eps) {  // 走到目标点了
            this.cells[0] = this.next_cell;  // 添加一个新蛇头
            this.next_cell = null;
            this.status = "idle";  // 走完了，停下来

            if (!this.check_tail_increasing()) {  // 蛇不变长
                this.cells.pop();  // 砍掉蛇尾
            }
        } else {
            const move_distance = this.speed * this.timedelata / 1000; // timedelta:每两帧之间多少毫秒，见GameObject.js
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;  // 计算蛇头坐标的实时变化方便渲染

            if (!this.check_tail_increasing()) {  // 若蛇不变长1格，就把蛇尾往前移动一下
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];  // 蛇尾和蛇倒二尾
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() {
        if (this.status === 'move') {
            this.update_move();
        }

        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        if (this.status === "die") {
            ctx.fillStyle = "white";
        }

        for (const cell of this.cells) { // of遍历值，in遍历下标
            ctx.beginPath(); // 起始一个路径
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2); //画圆弧（起始点，半径，起始角度，终止角度）
            ctx.fill(); //填充颜色
        }
        // 在每两个圆中间渲染方块，让蛇身更流畅
        for (let i = 1; i < this.cells.length; i ++ ) {
            const a = this.cells[i - 1], b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;  // 考虑当两个圆很接近时不用渲染方块了
            if (Math.abs(a.x - b.x) < this.eps) {  // 垂直方向上的两个圆
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else {  // 水平方向上的两个圆
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i ++ ) {   // 循环渲染两个眼睛
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;

            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}