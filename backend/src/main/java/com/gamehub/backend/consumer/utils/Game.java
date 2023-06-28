package com.gamehub.backend.consumer.utils;

import java.util.Random;

public class Game {
    final private Integer rows;
    final private Integer cols;
    final private Integer inner_walls_count;

    final private int[][] g;  // final 修饰的数组表示数组引用不可更改（比如初始化时final private int[][] g = new int[3][3];再给g = new int[4][4]; 将会导致编译错误。）但是数组元素仍然可以被修改。
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    public Game(Integer rows, Integer cols, Integer inner_walls_count) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
    }

    public int[][] getG() {
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0; // 每次退出一层递归都要恢复现场。
                    return true;
                }
            }
        }

        g[sx][sy] = 0;  // 用于恢复现场，因为g是全局变量，所有实例共享一份。之前在js前端写的时候是复制了一份g用于判断连通性，所以才没有这个恢复现场的问题
        return false;
    }

    private boolean draw() {  // 画地图
        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }

        for (int c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i ++ ) {
            for (int j = 0; j < 1000; j ++ ) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1- c] == 1)
                    continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (draw())
                break;
        }
    }
}
