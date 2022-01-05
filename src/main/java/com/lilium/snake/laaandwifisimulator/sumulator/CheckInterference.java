/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.util.LinkedList;

/**
 * 各AP,BSごとに干渉しているAP,BSのリストを作成するクラス
 *
 * @author ginnan
 */
public class CheckInterference {

    Area _area;
    WiFiAP[] _wifi_ap;
    LTEUBS[] _lteu_bs;

    public CheckInterference(Area area) {
        _area = area;
        _wifi_ap = area.getWiFiAP();
        _lteu_bs = area.getLTEUBS();
    }

    /* 対象のWiFi APが干渉しているAPのリストを取得する */
    public LinkedList WiFiCheck(int wifi_ap_id) {
        int overray_ap_id;
        LinkedList<Integer> interference_aps_list = new LinkedList<>();
        for (int i = 0; i < _wifi_ap[wifi_ap_id].overray_list.size(); i++) {
            overray_ap_id = _wifi_ap[wifi_ap_id].overray_list.get(i);
            if (overray_ap_id < 10000 && (_wifi_ap[overray_ap_id].assigned_channel == _wifi_ap[wifi_ap_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            } else if (overray_ap_id >= 10000 && (_lteu_bs[overray_ap_id - 10000].assigned_channel == _wifi_ap[wifi_ap_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            }
        }
        return interference_aps_list;
    }

    /* 対象のLTE-U BSが干渉しているAPのリストを取得する */
    public LinkedList LTEUCheck(int lteu_bs_id) {
        int overray_ap_id;
        LinkedList<Integer> interference_aps_list = new LinkedList<>();
        for (int i = 0; i < _lteu_bs[lteu_bs_id].overray_list.size(); i++) {
            overray_ap_id = _lteu_bs[lteu_bs_id].overray_list.get(i);
            if (overray_ap_id < 10000 && (_wifi_ap[overray_ap_id].assigned_channel == _lteu_bs[lteu_bs_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            } else if (overray_ap_id >= 10000 && (_lteu_bs[overray_ap_id].assigned_channel == _lteu_bs[lteu_bs_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            }
        }
        return interference_aps_list;
    }

    /* 対象のWiFi APが干渉しているAPのリストを取得する:アルゴリズム用 */
    public LinkedList WiFiCheck(int wifi_ap_id, WiFiAP[] wifiap, LTEUBS[] lteubs) {
        int overray_ap_id;
        LinkedList<Integer> interference_aps_list = new LinkedList<>();
        for (int i = 0; i < wifiap[wifi_ap_id].overray_list.size(); i++) {
            overray_ap_id = wifiap[wifi_ap_id].overray_list.get(i);
            if (overray_ap_id < 10000 && (wifiap[overray_ap_id].assigned_channel == wifiap[wifi_ap_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            } else if (overray_ap_id >= 10000 && (lteubs[overray_ap_id - 10000].assigned_channel == wifiap[wifi_ap_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            }
        }
        return interference_aps_list;
    }

    /* 対象のLTE-U BSが干渉しているAPのリストを取得する：アルゴリズム用 */
    public LinkedList LTEUCheck(int lteu_bs_id, WiFiAP[] wifiap, LTEUBS[] lteubs) {
        int overray_ap_id;
        LinkedList<Integer> interference_aps_list = new LinkedList<>();
        for (int i = 0; i < lteubs[lteu_bs_id].overray_list.size(); i++) {
            overray_ap_id = lteubs[lteu_bs_id].overray_list.get(i);
            if (overray_ap_id < 10000 && (wifiap[overray_ap_id].assigned_channel == lteubs[lteu_bs_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            } else if (overray_ap_id >= 10000 && (lteubs[overray_ap_id].assigned_channel == lteubs[lteu_bs_id].assigned_channel)) {
                interference_aps_list.add(overray_ap_id);
            }
        }
        return interference_aps_list;
    }

    /*対象のWiFi APについて、どのチャネルがどのくらい干渉しているかを探索し、最小の干渉チャネルを返す */
    public int WiFiLeastInterference(int wifi_ap_id, WiFiAP[] wifiap, LTEUBS[] lteubs) {
        int[] channel_interference = new int[Constants.CHANNEL_NUM];
        int overray_ap_id;

        for (int i = 0; i < wifiap[wifi_ap_id].overray_list.size(); i++) {
            overray_ap_id = wifiap[wifi_ap_id].overray_list.get(i);
            if (overray_ap_id < 10000 && wifiap[overray_ap_id].assigned_channel != -1) {
                channel_interference[wifiap[overray_ap_id].assigned_channel]++;
            } else if (overray_ap_id >= 10000 && (lteubs[overray_ap_id - 10000].assigned_channel != -1)) {
                channel_interference[lteubs[overray_ap_id - 10000].assigned_channel]++;
            }
        }

        int inteference_num = 99999;
        int select_channel = -1;

        for (int i = 0; i < channel_interference.length; i++) {
            if (inteference_num > channel_interference[i]) {
                inteference_num = channel_interference[i];
                select_channel = i;
            }
        }

        return select_channel;
    }

    /* 対象のLTE-Uについて、どのチャネルがどのくらい干渉しているかを探索し、最小の干渉チャネルを返す*/
    public int LTEULeastInterference(int lteu_bs_id, WiFiAP[] wifiap, LTEUBS[] lteubs) {
        int[] channel_interference = new int[Constants.CHANNEL_NUM];
        int overray_ap_id;

        for (int i = 0; i < lteubs[lteu_bs_id].overray_list.size(); i++) {
            overray_ap_id = lteubs[lteu_bs_id].overray_list.get(i);
            if (overray_ap_id < 10000 && wifiap[overray_ap_id].assigned_channel != -1) {
                channel_interference[wifiap[overray_ap_id].assigned_channel]++;
            } else if (overray_ap_id >= 10000 && (lteubs[overray_ap_id - 10000].assigned_channel != -1)) {
                channel_interference[lteubs[overray_ap_id - 10000].assigned_channel]++;
            }
        }

        int inteference_num = 99999;
        int select_channel = -1;

        for (int i = 0; i < channel_interference.length; i++) {
            if (inteference_num > channel_interference[i]) {
                inteference_num = channel_interference[i];
                select_channel = i;
            }
        }
        return select_channel;

    }

    //全く干渉していないチャネルを返す
    public int WiFiNoInterference(int wifi_ap_id, WiFiAP[] wifiap, LTEUBS[] lteubs) {
        int[] channel_interference = new int[Constants.CHANNEL_NUM];
        int overray_ap_id;

        for (int i = 0; i < wifiap[wifi_ap_id].overray_list.size(); i++) {
            overray_ap_id = wifiap[wifi_ap_id].overray_list.get(i);
            if (overray_ap_id < 10000 && wifiap[overray_ap_id].assigned_channel != -1) {
                channel_interference[wifiap[overray_ap_id].assigned_channel]++;
            } else if (overray_ap_id >= 10000 && (lteubs[overray_ap_id - 10000].assigned_channel != -1)) {
                channel_interference[lteubs[overray_ap_id - 10000].assigned_channel]++;
            }
        }

        int inteference_num = 99999;
        int select_channel = -1;

        for (int i = 0; i < channel_interference.length; i++) {
            if (channel_interference[i] == 0) {
                inteference_num = channel_interference[i];
                select_channel = i;
            }
        }

        return select_channel;
    }
}
