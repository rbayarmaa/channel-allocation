/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

/**
 * エリアごとのAPのカバー数を保持するクラス(提案手法で使用)
 *
 * @author ginnan
 */
public class APCover {
    private int area_id;//エリアのID
    private int cover_ap_num;//カバーしているAPの数

    public APCover(int area, int cover) {
        this.area_id = area;
        this.cover_ap_num = cover;
    }

    public int getAreaId() {
        return area_id;
    }

    public int getCoverAPNum() {
        return cover_ap_num;
    }
}
