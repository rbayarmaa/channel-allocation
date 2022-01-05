/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.util.LinkedList;

/**
 * AP,BSの選択方法 (スループットが最も高くなるAP or BSを選択)
 *
 * @author ginnan
 */
public class APBSselection {

    private final Scenario _scenario;
    private final WiFiAP _wifi_ap[];
    private final LTEUBS _lteu_bs[];

    public APBSselection(Scenario scenario) {
        _scenario = scenario;
        Area area = _scenario.getArea();
        _wifi_ap = area.getWiFiAP();
        _lteu_bs = area.getLTEUBS();
    }

    //WiFiのみユーザの選択
    public int SelectAP(UserNode user) {
        LinkedList<Integer> coverd_ap_list = user.getCoveredAPs();   //ユーザをカバーしているAPのリストを取得
        double[] throughput_list = new double[coverd_ap_list.size()];   //接続対象APに接続したときのスループットのリスト
        int[] ap_index_list = new int[coverd_ap_list.size()];   //接続対象となるAPのリスト
        double temp_capacity; //スループット計算のための一時的な変数
        double temp_user_num;   //スループット計算のための一時的な変数(接続中 + 新しい)
        double max_throughput = 0;
        int selected_ap_index = -1;  //接続先となるAP

        for (int j = 0; j < coverd_ap_list.size(); j++) {
            temp_user_num = _wifi_ap[coverd_ap_list.get(j)].connecting_num + 1;
            temp_capacity = _wifi_ap[coverd_ap_list.get(j)].capacity;
            throughput_list[j] = temp_capacity / temp_user_num;
            ap_index_list[j] = coverd_ap_list.get(j);
        }

        //得られるスループットが最大のものを選ぶ
        for (int j = 0; j < throughput_list.length; j++) {
            if (max_throughput < throughput_list[j]) {
                max_throughput = throughput_list[j];
                selected_ap_index = ap_index_list[j];
            }
        }

        return selected_ap_index;
    }

    //LTE+WiFiユーザ用
    public int SelectAPorBS(UserNode user) {
        LinkedList<Integer> coverd_ap_list = user.getCoveredAPs();
        double[] throughput_list = new double[coverd_ap_list.size()];   //接続対象APに接続したときのスループットのリスト
        int[] ap_index_list = new int[coverd_ap_list.size()];   //接続対象となるAPのリスト
        double temp_capacity; //スループット計算のための一時的な変数
        double temp_user_num;   //スループット計算のための一時的な変数(接続中 + 新しい)
        double max_throughput = 0;
        int selected_ap_index = 0;  //接続先となるAP

        for (int j = 0; j < coverd_ap_list.size(); j++) {
            if (coverd_ap_list.get(j) < 10000) {
                temp_user_num = _wifi_ap[coverd_ap_list.get(j)].connecting_num + 1;
                temp_capacity = _wifi_ap[coverd_ap_list.get(j)].capacity;
                throughput_list[j] = temp_capacity / temp_user_num;
                ap_index_list[j] = coverd_ap_list.get(j);
            } else {
                temp_user_num = _lteu_bs[coverd_ap_list.get(j) - 10000].connecting_num + 1;
                temp_capacity = _lteu_bs[coverd_ap_list.get(j) - 10000].capacity;
                throughput_list[j] = temp_capacity / temp_user_num;
                ap_index_list[j] = coverd_ap_list.get(j);
            }
        }

        for (int j = 0; j < throughput_list.length; j++) {
            if (max_throughput <= throughput_list[j]) {
                max_throughput = throughput_list[j];
                selected_ap_index = ap_index_list[j];
            }
        }
        return selected_ap_index;
    }

    /* LTEBSの中から選択:提案手法の接続先選択で利用 */
    public int SelectBS(UserNode user) {
        LinkedList<Integer> coverd_ap_list = user.getCoveredAPs();
        double[] throughput_list = new double[coverd_ap_list.size()];   //接続対象APに接続したときのスループットのリスト
        int[] ap_index_list = new int[coverd_ap_list.size()];   //接続対象となるAPのリスト
        double temp_capacity; //スループット計算のための一時的な変数
        double temp_user_num;   //スループット計算のための一時的な変数(接続中 + 新しい)
        double max_throughput = 0;
        int selected_ap_index = 0;  //接続先となるAP

        for (int j = 0; j < coverd_ap_list.size(); j++) {
            if (coverd_ap_list.get(j) >= 10000) {

                temp_user_num = _lteu_bs[coverd_ap_list.get(j) - 10000].connecting_num + 1;
                temp_capacity = _lteu_bs[coverd_ap_list.get(j) - 10000].capacity;
                throughput_list[j] = temp_capacity / temp_user_num;
                ap_index_list[j] = coverd_ap_list.get(j);
            }
        }

        for (int j = 0; j < throughput_list.length; j++) {
            if (max_throughput <= throughput_list[j]) {
                max_throughput = throughput_list[j];
                selected_ap_index = ap_index_list[j];
            }
        }

        return selected_ap_index;
    }

    /*接続先変更手法の3番目の手順で利用*/
    public int SelectAPorBSforAlgorithm(UserNode user) {
        LinkedList<Integer> coverd_ap_list = user.getCoveredAPs();
        double[] throughput_list = new double[coverd_ap_list.size()];   //接続対象APに接続したときのスループットのリスト
        int[] ap_index_list = new int[coverd_ap_list.size()];   //接続対象となるAPのリスト
        double temp_capacity; //スループット計算のための一時的な変数
        double temp_user_num;   //スループット計算のための一時的な変数(接続中 + 新しい)
        double max_throughput = 0;
        int selected_ap_index = -1;  //接続先となるAP

//        int now_ap_index = -1;

        for (int j = 0; j < coverd_ap_list.size(); j++) {
            if (coverd_ap_list.get(j) < 10000) {
                temp_user_num = _wifi_ap[coverd_ap_list.get(j)].connecting_num + 1;
                temp_capacity = _wifi_ap[coverd_ap_list.get(j)].capacity;
                throughput_list[j] = temp_capacity / temp_user_num;
                ap_index_list[j] = coverd_ap_list.get(j);
            } else {
                if (user.getConnectedAP().ap_id == coverd_ap_list.get(j)) {
                    temp_user_num = _lteu_bs[coverd_ap_list.get(j) - 10000].connecting_num;
                    temp_capacity = _lteu_bs[coverd_ap_list.get(j) - 10000].capacity;
//                    now_ap_index = j;
                    selected_ap_index = coverd_ap_list.get(j);
                } else {//他のLTe-Uには接続したくない
                    temp_user_num = 1;//_lteu_bs[coverd_ap_list.get(j) - 10000].connecting_num + 1000000;
                    temp_capacity = -100000;
                }

                throughput_list[j] = temp_capacity / temp_user_num;
                ap_index_list[j] = coverd_ap_list.get(j);
            }
        }

//        double near_large_thr = 99999;
//
//        for (int j = 0; j < throughput_list.length; j++) {
//            if (throughput_list[now_ap_index] < throughput_list[j] && throughput_list[j] <= near_large_thr) {
//                near_large_thr = throughput_list[j];
//                selected_ap_index = ap_index_list[j];
//
//            }
//        }

        for (int j = 0; j < throughput_list.length; j++) {
            if (max_throughput <= throughput_list[j]) {
                max_throughput = throughput_list[j];
                selected_ap_index = ap_index_list[j];
            }
        }
//        System.out.println(selected_ap_index);
        return selected_ap_index;
    }

}
