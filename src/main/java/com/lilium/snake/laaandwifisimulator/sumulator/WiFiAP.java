/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

/**
 * WiFi APの情報
 *
 * @author ginnan
 */
public class WiFiAP extends AccessPoint {

    private final int wifi_ap_id;

    //    private final int located_check; // 配置されたかの確認(最初に配置するときに利用)
    public WiFiAP(int ap_id) {
        super(ap_id);

        this.wifi_ap_id = ap_id;

//        SetCapacity();
        max_capacity = Constants.WiFi_CAPACITY;//使っていない
        wifi_lteu_indicator = 0;

//        located_check = 1;
    }

    //WiFiの容量をセット
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

            if (lteu_count != 0) {
                capacity = Constants.CAPACITY_WITH_LAA_WIFI[0][wifi_count + 1] / (1 + wifi_count);
            } else {
                capacity = Constants.CAPACITY_WITH_WIFIS[wifi_count] / (1 + wifi_count);
            }
        }
    }
}
