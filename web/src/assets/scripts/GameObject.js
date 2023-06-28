//实现每秒钟所有游戏对象都刷新60次
const GAME_OBJECTS = [];

export class GameObjects {
    constructor() {
        GAME_OBJECTS.push(this);
        this.timedelata = 0;
        this.has_called_start = false;
    }

    start() {  //创建时执行一次

    }

    update() {  //每一帧执行一次，除了第一帧之外

    }

    on_destroy() {  //删除之前要执行的功能

    }

    destory() {
        this.on_destroy();

        for (let i in GAME_OBJECTS) {
            const obj = GAME_OBJECTS[i];
            if (obj === this) {
                GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

let last_timestamp;  //上一次执行的时刻
const step = timestamp => {
    for (let obj of GAME_OBJECTS) {  //of遍历值，in遍历下标
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        } else {
            obj.timedelata = timestamp - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp;
    requestAnimationFrame(step)
}

requestAnimationFrame(step)