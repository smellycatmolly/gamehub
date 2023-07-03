import { GameObjects } from "./GameObject";
import { Wall } from "./Wall";
import { Snake } from "./Snake";

export class GameMap extends GameObjects {
    constructor(ctx, parent, store) { // 动态修改画布的长宽
        super(); // 执行了GameOjects的构造函数，GameMap首先被加到了GAME_OBJECTS的数组里面//一旦被加到数组里requestAnimationFrame(step)就会update、render这个GameMap
        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0; // 绝对距离，每个格子的长宽

        this.rows = 13; // 如果都是长宽13，两条蛇起点分别是（11，1）（1，11）每走一步两条蛇的横竖坐标和都是偶奇偶
        this.cols = 14; // 如果正方形地图会导致在两者即将进入碰撞点时，弱者想碰撞想平手故意平手，减弱了强者的优势
        // 所以把cols调成14：避免走到一个格子上，这样长方形地图将采用中心对称放障碍物：左右对称+上下对称

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ];
    }
    // 生成地图的逻辑代码会放到后端，避免玩家改网页代码自己生成地图
    create_walls() {  //墙是在GameMap之后被加进GAME_OBJECTS数组里的，所以会覆盖绿色map
        const g = this.store.state.pk.gamemap;

        for (let r = 0; r < this.rows; r ++ ) {
            for (let c = 0; c < this.cols; c ++ ) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }

    add_listening_events() {
        console.log(this.store.state.record);

        if (this.store.state.record.is_record) {
            let k = 0;

            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.record.record_loser;
            const [snake0, snake1] = this.snakes;
            const interval_id = setInterval(() => {
                if (k >= a_steps.length - 1) {
                    if (loser === "all" || loser === "A") {
                        snake0.status = "die";
                    }
                    if (loser === "all" || loser === "B") {
                        snake1.status = "die";
                    }
                    clearInterval(interval_id);
                } else {
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k ++ ;
            }, 300);
        } else {
            this.ctx.canvas.focus();

            this.ctx.canvas.addEventListener("keydown", e => {
                let d = -1;
                if (e.key === 'w') d = 0;
                else if (e.key === 'd') d = 1;
                else if (e.key === 's') d = 2;
                else if (e.key === 'a') d = 3;

                if (d >= 0) {
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        direction: d,
                    }));
                }
            });
        }
    }

    start() {
        this.create_walls();
        
        this.add_listening_events();
    }

    update_size() {  // 保证浏览器页面大小拖动时，地图大小也跟随变化
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() {    // 判断两条蛇是否都准备好下一回合了
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false; // 如果死亡或者正在移动，就无法进入下一回合
            if (snake.direction === -1) return false; // 如果没有指令，也无法进入下个回合
        }
        return true; // idle且有指令
    }

    next_step() {   // 让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }
    // 检测目标位置是否合法：没有撞到两条蛇的身体和障碍物
    check_valid(cell) {  //非法判断不能写在Snake.js里，因为不能既当裁判又当运动员
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;
            if (!snake.check_tail_increasing()) {  // 当蛇尾会前进时，蛇尾不在判断范围里
                k -- ;  // 在下面的循环去掉了蛇尾的判断
            }
            for (let i = 0; i < k; i ++ ) {
                if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }

        return true;
    }

    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
        this.render(); //每帧执行一次渲染
    }

    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r ++ ) {
            for (let c = 0; c < this.cols; c ++ ) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}