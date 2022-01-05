/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

/**
 * LTE BSの情報
 *
 * @author ginnan
 */
public class LTEUBS extends AccessPoint {

    private final int lteu_bs_id;


    public LTEUBS(int ap_id) {
        super(ap_id);

        this.lteu_bs_id = ap_id;

//        SetCapacity();
        max_capacity = Constants.LTEU_CAPACITY; //使っていない
        wifi_lteu_indicator = 1;

    }

    //BSの容量をセット
    public void SetCapacity() {
        if (this.assigned_channel == -1) {
            capacity = -1000000;
        } else {
            int lteu_count = 0;
            int wifi_count = 0;
            for (int j = 0; j < interference_list.size(); j++) {
                if (interference_list.get(j) >= 10000) {
                    lteu_count++;
                } else {
                    wifi_count++;
                }
            }
            //現状だと、lteu_countは0になるはず

            capacity = Constants.CAPACITY_WITH_LAA_WIFI[1][wifi_count];
        }
    }
}
