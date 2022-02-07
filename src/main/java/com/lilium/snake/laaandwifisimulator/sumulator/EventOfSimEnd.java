/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.util.List;
import java.util.ListIterator;

/**
 * シミュレーション終了イベント
 *
 * @author ginnan
 */
public class EventOfSimEnd extends Event {

    public EventOfSimEnd(Scenario scenario) {
        super(scenario);
        event_time = _queue.getCurrentTime();
    }

    @Override
    public void runEvent() {
        simEnd();
    }

    public void simEnd() {//

        // 系内滞在呼の処理は一定時間通信する場合は不必要

        /* 系内滞在ユーザの処理 */
        List<UserNode> User_List;
        double thr_time_num[] = new double[6];
        double min_thr_time[] = new double[4];
        double max_thr_time[] = new double[4];

        // WiFiのみユーザの処理
        double wifi_sum_throughput = 0.0;
        double wifi_sum_transtime = 0.0;
        int wifi_stay_user_num = 0;

        double wifi_min_throughput = 99999;
        double wifi_min_transtime = 99999;

        double wifi_max_throughput = 0.0;
        double wifi_max_transtime = 0.0;

        // WiFi + LTE-Uユーザの処理
        double lteu_sum_throughput = 0.0;
        double lteu_sum_transtime = 0.0;
        int lteu_stay_user_num = 0;

        double lteu_min_throughput = 99999;
        double lteu_min_transtime = 99999;

        double lteu_max_throughput = 0.0;
        double lteu_max_transtime = 0.0;

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            User_List = _area.getWiFiAP(i).UserList;
            thr_time_num = plusThrTimeNum(User_List);
            wifi_sum_throughput += thr_time_num[0];
            wifi_sum_transtime += thr_time_num[1];
            wifi_stay_user_num += thr_time_num[2];
            lteu_sum_throughput += thr_time_num[3];
            lteu_sum_transtime += thr_time_num[4];
            lteu_stay_user_num += thr_time_num[5];

            min_thr_time = minThrTimeNum(User_List);
            if (wifi_min_throughput >= min_thr_time[0]) {
                wifi_min_throughput = min_thr_time[0];
            }

            if (wifi_min_transtime >= min_thr_time[1]) {
                wifi_min_transtime = min_thr_time[1];
            }

            if (lteu_min_throughput >= min_thr_time[2]) {
                lteu_min_throughput = min_thr_time[2];
            }

            if (lteu_min_transtime >= min_thr_time[3]) {
                lteu_min_transtime = min_thr_time[3];
            }

            max_thr_time = maxThrTimeNum(User_List);
            if (wifi_max_throughput <= max_thr_time[0]) {
                wifi_max_throughput = max_thr_time[0];
            }

            if (wifi_max_transtime <= max_thr_time[1]) {
                wifi_max_transtime = max_thr_time[1];
            }

            if (lteu_max_throughput <= max_thr_time[2]) {
                lteu_max_throughput = max_thr_time[2];
            }

            if (lteu_max_transtime <= max_thr_time[3]) {
                lteu_max_transtime = max_thr_time[3];
            }

        }

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            User_List = _area.getLTEUBS(i).UserList;
            thr_time_num = plusThrTimeNum(User_List);
            wifi_sum_throughput += thr_time_num[0];
            wifi_sum_transtime += thr_time_num[1];
            wifi_stay_user_num += thr_time_num[2];
            lteu_sum_throughput += thr_time_num[3];
            lteu_sum_transtime += thr_time_num[4];
            lteu_stay_user_num += thr_time_num[5];

            min_thr_time = minThrTimeNum(User_List);
            if (wifi_min_throughput >= min_thr_time[0]) {
                wifi_min_throughput = min_thr_time[0];
            }

            if (wifi_min_transtime >= min_thr_time[1]) {
                wifi_min_transtime = min_thr_time[1];
            }

            if (lteu_min_throughput >= min_thr_time[2]) {
                lteu_min_throughput = min_thr_time[2];
            }

            if (lteu_min_transtime >= min_thr_time[3]) {
                lteu_min_transtime = min_thr_time[3];
            }

            max_thr_time = maxThrTimeNum(User_List);
            if (wifi_max_throughput <= max_thr_time[0]) {
                wifi_max_throughput = max_thr_time[0];
            }

            if (wifi_max_transtime <= max_thr_time[1]) {
                wifi_max_transtime = max_thr_time[1];
            }

            if (lteu_max_throughput <= max_thr_time[2]) {
                lteu_max_throughput = max_thr_time[2];
            }

            if (lteu_max_transtime <= max_thr_time[3]) {
                lteu_max_transtime = max_thr_time[3];
            }

        }
        // シミュレーション終了時にサービスを終了していないユーザの統計(※一定時間通信する場合は使わない)
        _scenario.getData().updateNotEndUser(wifi_sum_throughput, lteu_sum_throughput, wifi_sum_transtime,
                lteu_sum_transtime, wifi_stay_user_num, lteu_stay_user_num);

        _scenario.getData().updateNotEndUseMin(wifi_min_throughput, lteu_min_throughput, wifi_min_transtime,
                lteu_min_transtime);

        /* シミュレーションの終了時刻 */
        _scenario.getData().updateEndTime(_queue.getCurrentTime());

        /* イベントキューを空に */
        _queue.cleanEventQueue();
    }

    /* 系内滞在呼のスループット、通信時間、ユーザ数を計算 */
    private double[] plusThrTimeNum(List<UserNode> list) {

        double wifi_sum_thr = 0.0;
        double wifi_sum_trans = 0.0;
        double wifi_num = 0;

        double lteu_sum_thr = 0.0;
        double lteu_sum_trans = 0.0;
        double lteu_num = 0;

        ListIterator<UserNode> ite;
        ite = list.listIterator();
        UserNode user = null;

        if (_param.end_condition == 1) {/* 終了条件が時間 */

        } else {/* 終了条件が呼数 */
            while (ite.hasNext()) {
                user = ite.next();
                if (user.user_set == 0) {
                    if (user.wifi_node_id < _param.end_num + Constants.STEADY_NUM
                            && user.wifi_node_id >= Constants.STEADY_NUM) {// System.out.println("oi"+
                                                                           // user.wifi_node_id);
                        wifi_sum_thr += user.getAverageThroughput(event_time);
                        wifi_sum_trans += user.getTransTime(event_time);
                        wifi_num++;
                    }

                } else {
                    if (user.lteu_node_id < _param.end_num + Constants.STEADY_NUM
                            && user.lteu_node_id >= Constants.STEADY_NUM) {// System.out.println("oioi"+user.lteu_node_id);
                        lteu_sum_thr += user.getAverageThroughput(event_time);
                        lteu_sum_trans += user.getTransTime(event_time);
                        lteu_num++;
                    }
                }
            }
        } // System.out.println(wifi_num + "\t" + lteu_num);
        double thr_time_num[] = new double[6];
        thr_time_num[0] = wifi_sum_thr;
        thr_time_num[1] = wifi_sum_trans;
        thr_time_num[2] = wifi_num;
        thr_time_num[3] = lteu_sum_thr;
        thr_time_num[4] = lteu_sum_trans;
        thr_time_num[5] = lteu_num;

        return thr_time_num;
    }

    /* 系内滞在呼の最小スループット、最小通信時間を計算 */
    private double[] minThrTimeNum(List<UserNode> list) {

        double wifi_min_thr = 99999;
        double wifi_min_trans = 99999;

        double lteu_min_thr = 99999;
        double lteu_min_trans = 99999;

        ListIterator<UserNode> ite;
        ite = list.listIterator();
        UserNode user = null;

        if (_param.end_condition == 1) {/* 終了条件が時間 */

        } else {/* 終了条件が呼数 */
            while (ite.hasNext()) {
                user = ite.next();
                if (user.user_set == 0) {
                    if (user.wifi_node_id < _param.end_num + Constants.STEADY_NUM
                            && user.wifi_node_id >= Constants.STEADY_NUM) {// System.out.println("oi"+
                                                                           // user.wifi_node_id);
                        if (wifi_min_thr >= user.getAverageThroughput(event_time)) {
                            wifi_min_thr = user.getAverageThroughput(event_time);
                        }

                        if (wifi_min_trans >= user.getTransTime(event_time)) {
                            wifi_min_trans = user.getTransTime(event_time);
                        }
                    }

                } else {
                    if (user.lteu_node_id < _param.end_num + Constants.STEADY_NUM
                            && user.lteu_node_id >= Constants.STEADY_NUM) {// System.out.println("oioi"+user.lteu_node_id);
                        if (lteu_min_thr >= user.getAverageThroughput(event_time)) {
                            lteu_min_thr = user.getAverageThroughput(event_time);
                        }

                        if (lteu_min_trans >= user.getTransTime(event_time)) {
                            lteu_min_trans = user.getTransTime(event_time);
                        }
                    }
                }
            }
        } // System.out.println(wifi_num + "\t" + lteu_num);

        double thr_time_num[] = new double[4];
        thr_time_num[0] = wifi_min_thr;
        thr_time_num[1] = wifi_min_trans;
        thr_time_num[2] = lteu_min_thr;
        thr_time_num[3] = lteu_min_trans;

        return thr_time_num;
    }

    /* 系内滞在呼の最大スループット、最大通信時間を計算 */
    private double[] maxThrTimeNum(List<UserNode> list) {

        double wifi_max_thr = 0.0;
        double wifi_max_trans = 0.0;

        double lteu_max_thr = 0.0;
        double lteu_max_trans = 0.0;

        ListIterator<UserNode> ite;
        ite = list.listIterator();
        UserNode user = null;

        if (_param.end_condition == 1) {/* 終了条件が時間 */

        } else {/* 終了条件が呼数 */
            while (ite.hasNext()) {
                user = ite.next();
                if (user.user_set == 0) {
                    if (user.wifi_node_id < _param.end_num + Constants.STEADY_NUM
                            && user.wifi_node_id >= Constants.STEADY_NUM) {// System.out.println("oi"+
                                                                           // user.wifi_node_id);
                        if (wifi_max_thr <= user.getAverageThroughput(event_time)) {
                            wifi_max_thr = user.getAverageThroughput(event_time);
                        }

                        if (wifi_max_trans <= user.getTransTime(event_time)) {
                            wifi_max_trans = user.getTransTime(event_time);
                        }
                    }

                } else {
                    if (user.lteu_node_id < _param.end_num + Constants.STEADY_NUM
                            && user.lteu_node_id >= Constants.STEADY_NUM) {// System.out.println("oioi"+user.lteu_node_id);
                        if (lteu_max_thr <= user.getAverageThroughput(event_time)) {
                            lteu_max_thr = user.getAverageThroughput(event_time);
                        }

                        if (lteu_max_trans <= user.getTransTime(event_time)) {
                            lteu_max_trans = user.getTransTime(event_time);
                        }
                    }
                }
            }
        } // System.out.println(wifi_num + "\t" + lteu_num);

        double thr_time_num[] = new double[4];
        thr_time_num[0] = wifi_max_thr;
        thr_time_num[1] = wifi_max_trans;
        thr_time_num[2] = lteu_max_thr;
        thr_time_num[3] = lteu_max_trans;

        return thr_time_num;
    }
}
