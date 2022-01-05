/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 1ループ中の統計情報を管理するクラス
 *
 * @author ginnan
 */
public class DataOfOneSim {

    /* Network-wide statistics */
    public double ave_throughput;
    public double ave_transtime;
    public int target_end_num; //サービスを終了
    public int target_connected_num;
    public double end_time;
    public double sim_start_time; //シミュレーション開始時間(統計対象から)


    /* WiFi only user statistics */
    public double ave_throughput_wifi;
    public double ave_transtime_wifi;
    public int target_wifi_end_num; //Number of users only for wifi that ended the service
    public int target_wifi_connected_num;

    /* WiFi + LTE-U user statistics */
    public double ave_throughput_lteu;
    public double ave_transtime_lteu;
    public int target_lteu_end_num; //サービスを終了したwifi+lteuユーザ数
    public int target_lteu_connected_num;

    /* Save minimum */
    public double min_throughput;
    public double min_trans_time;
    public double min_wifi_throughput;
    public double min_wifi_trans_time;
    public double min_lteu_throughput;
    public double min_lteu_trans_time;

    /* Save maximum */
    public double max_throughput;
    public double max_trans_time;
    public double max_wifi_throughput;
    public double max_wifi_trans_time;
    public double max_lteu_throughput;
    public double max_lteu_trans_time;

    /* When communicating for a certain period of time*/
    public double avg_provided_data_size;
    public double avg_provided_data_size_wifi;
    public double avg_provided_data_size_lteu;

    /* Dispersion of throughput fluctuations during user communication*/
    public double variance_per_user;
    public double variance_per_user_wifi;
    public double variance_per_user_lteu;

    /* Distribution of average user throughput */
    public double variance;
    public double variance_wifi;
    public double variance_lteu;

    //Record throughput for each user
    public final double[] wifi_users_throghput;
    public final double[] lteu_users_throghput;

    //Record minimum throughput per user
    public final double[] wifi_users_min_throghput;
    public final double[] lteu_users_min_throghput;

    public final double[] wifi_users_min_time_throghput;
    public final double[] lteu_users_min_time_throghput;

    /* Record the time average of minimum throughput */
    public double min_thr_time_avg;
    public double min_thr_time_avg_wifi;
    public double min_thr_time_avg_lteu;

    public double min_throughput_time;
    public double min_throughput_time_wifi;
    public double min_throughput_time_lteu;

    public double temp_min_throughput;
    public double temp_min_throughput_wifi;
    public double temp_min_throughput_lteu;

    public double previous_min_time;
    public double previous_min_time_wifi;
    public double previous_min_time_lteu;

    public double non_user_time;
    public double non_user_time_wifi;
    public double non_user_time_lteu;

    //fairness index
    public double fairness_index_avg;
    public double fairness_index_avg_wifi;
    public double fairness_index_avg_lteu;

    public double thr_2_jo_avg;
    public double thr_2_jo_avg_wifi;
    public double thr_2_jo_avg_lteu;

    public double fairness_index_min;
    public double fairness_index_min_wifi;
    public double fairness_index_min_lteu;

    public double thr_2_jo_min;
    public double thr_2_jo_min_wifi;
    public double thr_2_jo_min_lteu;
    public int lte_num; //Number of LTE + WiFi users connected to LTE
    public int wifi_num; //LTE + WiFi ユーザのうち、WiFiに接続したユーザ数


    /* Update of statistical information at the end of the target user */
    public int updateTargetUser(UserNode user) throws IOException {
        target_end_num++;

        if (target_end_num == 1) {//Record of the first user (record of the start of simulation))
            sim_start_time = user.getEndTime() - user.getTransTime();
            previous_min_time = sim_start_time;
            previous_min_time_wifi = sim_start_time;
            previous_min_time_lteu = sim_start_time;
        }

        if (user.user_set == 0) {
            target_wifi_end_num++;
        } else {
            target_lteu_end_num++;
        }

        //Output for confirmation
        if (target_end_num % 50000 == 0) {
            System.out.println(target_end_num + " users ended. -> " + "wifi: " + target_wifi_end_num + " lte-u: " + target_lteu_end_num);
        }

        int sum = 0;
        double temp_variance = 0.0;
        //Variance calculation process
        for (int i = 0; i < user.getThroughputs().size(); i++) {
            sum += (double) user.getThroughputs().get(i);
        }

        sum = sum / user.getThroughputs().size();

        for (int i = 0; i < user.getThroughputs().size(); i++) {
            temp_variance += ((double) user.getThroughputs().get(i) - sum) * ((double) user.getThroughputs().get(i) - sum);
        }
        temp_variance = temp_variance / user.getThroughputs().size();
        variance_per_user += temp_variance;
        //Completion of variance calculation process

        if (Constants.SERVICE_SET == 0) {

            //Processing of all users
            target_connected_num++;

            ave_throughput += user.getAverageThroughput();
            ave_transtime += user.getTransTime();

            //Minimum value processing
            if (min_throughput >= user.getAverageThroughput()) {
                min_throughput = user.getAverageThroughput();
            }

            if (min_trans_time >= user.getTransTime()) {
                min_trans_time = user.getTransTime();
            }

            //Maximum value processing
            if (max_throughput <= user.getAverageThroughput()) {
                max_throughput = user.getAverageThroughput();
            }

            if (max_trans_time <= user.getTransTime()) {
                max_trans_time = user.getTransTime();
            }

            //Processing for WiFi only users or WiFi + LTE-U BS users
            if (user.user_set == 0) {//WiFi only user processing

                wifi_users_throghput[target_wifi_connected_num] = user.getAverageThroughput();

                target_wifi_connected_num++;

                ave_throughput_wifi += user.getAverageThroughput();
                ave_transtime_wifi += user.getTransTime();

                if (min_wifi_throughput >= user.getAverageThroughput()) {
                    min_wifi_throughput = user.getAverageThroughput();
                }

                if (min_wifi_trans_time >= user.getTransTime()) {
                    min_wifi_trans_time = user.getTransTime();
                }

                if (max_wifi_throughput <= user.getAverageThroughput()) {
                    max_wifi_throughput = user.getAverageThroughput();
                }

                if (max_wifi_trans_time <= user.getTransTime()) {
                    max_wifi_trans_time = user.getTransTime();
                }

                variance_per_user_wifi += temp_variance;

            } else {//WiFi + LTE-U only User processing

                lteu_users_throghput[target_lteu_connected_num] = user.getAverageThroughput();

                target_lteu_connected_num++;

                ave_throughput_lteu += user.getAverageThroughput();
                ave_transtime_lteu += user.getTransTime();

                if (min_lteu_throughput >= user.getAverageThroughput()) {
                    min_lteu_throughput = user.getAverageThroughput();
                }

                if (min_lteu_trans_time >= user.getTransTime()) {
                    min_lteu_trans_time = user.getTransTime();
                }

                if (max_lteu_throughput <= user.getAverageThroughput()) {
                    max_lteu_throughput = user.getAverageThroughput();
                }

                if (max_lteu_trans_time <= user.getTransTime()) {
                    max_lteu_trans_time = user.getTransTime();
                }

                variance_per_user_lteu += temp_variance;

                /* Record which LTEL WiFi user was connected to */
                lte_wifi_num_count(user);
            }

        } else {//Statistical processing when communicating for a certain period of time
            //Overall processing
            ave_throughput += user.getAverageThroughput2();
            ave_transtime += user.getTransTime();
            avg_provided_data_size += user.getProvidedDataSize();

            thr_2_jo_avg += Math.pow(user.getAverageThroughput2(), 2);

//            min_thr_time_avg += user.getMinThroghputTimeAverage();
            target_connected_num++;

            //The minimum throughput returns the smallest throughput obtained by the user ⇒ it is the smallest among the users
            if (min_throughput >= user.getMinThroughput()) {
                min_throughput = user.getMinThroughput();

                if (min_throughput_time <= user.getMinThroghputTime()) {
                    min_throughput_time = user.getMinThroghputTime();
                }

            }

            thr_2_jo_min += Math.pow(user.getMinThroughput(), 2);
            fairness_index_min += user.getMinThroughput();

            if (user.user_set == 0) {//WiFi only user processing

                wifi_users_throghput[target_wifi_connected_num] = user.getAverageThroughput2();

                wifi_users_min_throghput[target_wifi_connected_num] = user.getMinThroughput();

                thr_2_jo_min_wifi += Math.pow(user.getMinThroughput(), 2);
                fairness_index_min_wifi += user.getMinThroughput();

                //  wifi_users_min_time_throghput[target_wifi_connected_num] = user.getMinThroghputTimeAverage();

//                min_thr_time_avg_wifi += user.getMinThroghputTimeAverage();
                target_wifi_connected_num++;

                ave_throughput_wifi += user.getAverageThroughput2();
                avg_provided_data_size_wifi += user.getProvidedDataSize();

                thr_2_jo_avg_wifi += Math.pow(user.getAverageThroughput2(), 2);

                if (min_wifi_throughput >= user.getMinThroughput()) {
                    min_wifi_throughput = user.getMinThroughput();

                    if (min_throughput_time_wifi <= user.getMinThroghputTime()) {
                        min_throughput_time_wifi = user.getMinThroghputTime();
                    }

                }

                variance_per_user_wifi += temp_variance;

            } else {

                lteu_users_throghput[target_lteu_connected_num] = user.getAverageThroughput2();

                lteu_users_min_throghput[target_lteu_connected_num] = user.getMinThroughput();

                thr_2_jo_min_lteu += Math.pow(user.getMinThroughput(), 2);
                fairness_index_min_lteu += user.getMinThroughput();

                // lteu_users_min_time_throghput[target_lteu_connected_num] = user.getMinThroghputTimeAverage();

//                min_thr_time_avg_lteu += user.getMinThroghputTimeAverage();
                target_lteu_connected_num++;

                ave_throughput_lteu += user.getAverageThroughput2();
                avg_provided_data_size_lteu += user.getProvidedDataSize();

                thr_2_jo_avg_lteu += Math.pow(user.getAverageThroughput2(), 2);

                if (min_lteu_throughput >= user.getMinThroughput()) {
                    min_lteu_throughput = user.getMinThroughput();

                    if (min_throughput_time_lteu <= user.getMinThroghputTime()) {
                        min_throughput_time_lteu = user.getMinThroghputTime();
                    }

                }

                variance_per_user_lteu += temp_variance;

                /* Record which LTEL WiFi user was connected to*/
                lte_wifi_num_count(user);

            }
        }

        if (user.user_set == 0) {
            return target_wifi_end_num;
        } else {
            return target_lteu_end_num;
        }
    }

    /* Processing of users staying in the system at the end of simulation */
    public void updateNotEndUser(double wifi_sum_throughput, double lteu_sum_throughput, double wifi_sum_transtime,
                                 double lteu_sum_transtime, int wifi_stay_user_num, int lteu_stay_user_num) {
        //The entire network
        ave_throughput += (wifi_sum_throughput + lteu_sum_throughput);
        ave_transtime += (wifi_sum_transtime + lteu_sum_transtime);
        target_connected_num += (wifi_stay_user_num + lteu_stay_user_num);

        //WiFi only users
        ave_throughput_wifi += wifi_sum_throughput;
        ave_transtime_wifi += wifi_sum_transtime;
        target_wifi_connected_num += wifi_stay_user_num;

        //WiFi + LTE-Uユーザ
        ave_throughput_lteu += lteu_sum_throughput;
        ave_transtime_lteu += lteu_sum_transtime;
        target_lteu_connected_num += lteu_stay_user_num;
    }

    /* Processing of users staying in the system at the end of simulation*/
    public void updateNotEndUseMin(double wifi_min_throughput, double lteu_min_throughput, double wifi_min_transtime,
                                   double lteu_min_transtime) {
        //The entire network

        if (min_throughput >= wifi_min_throughput) {
            min_throughput = wifi_min_throughput;
        }

        if (min_throughput >= lteu_min_throughput) {
            min_throughput = lteu_min_throughput;
        }

        if (min_trans_time >= wifi_min_throughput) {
            min_trans_time = wifi_min_throughput;
        }

        if (min_trans_time >= lteu_min_transtime) {
            min_trans_time = lteu_min_transtime;
        }

        //WiFi only users
        if (min_wifi_throughput >= wifi_min_throughput) {
            min_wifi_throughput = wifi_min_throughput;
        }

        if (min_wifi_trans_time >= wifi_min_transtime) {
            min_wifi_trans_time = wifi_min_transtime;
        }

        //WiFi + LTE-Uユーザ
        if (min_lteu_throughput >= lteu_min_throughput) {
            min_lteu_throughput = lteu_min_throughput;
        }

        if (min_lteu_trans_time >= lteu_min_transtime) {
            min_lteu_trans_time = lteu_min_transtime;
        }

    }

    /* Processing of users staying in the system at the end of simulation */
    public void updateNotEndUseMax(double wifi_max_throughput, double lteu_max_throughput, double wifi_max_transtime,
                                   double lteu_max_transtime) {
        //The entire network

        if (max_throughput <= wifi_max_throughput) {
            max_throughput = wifi_max_throughput;
        }

        if (max_throughput <= lteu_max_throughput) {
            max_throughput = lteu_max_throughput;
        }

        if (max_trans_time <= wifi_max_throughput) {
            max_trans_time = wifi_max_throughput;
        }

        if (max_trans_time <= lteu_max_transtime) {
            max_trans_time = lteu_max_transtime;
        }

        //WiFiのみユーザ       
        if (max_wifi_throughput <= wifi_max_throughput) {
            max_wifi_throughput = wifi_max_throughput;
        }

        if (max_wifi_trans_time <= wifi_max_transtime) {
            max_wifi_trans_time = wifi_max_transtime;
        }

        //WiFi + LTE-Uユーザ
        if (max_lteu_throughput <= lteu_max_throughput) {
            max_lteu_throughput = lteu_max_throughput;
        }

        if (max_lteu_trans_time <= lteu_max_transtime) {
            max_lteu_trans_time = lteu_max_transtime;
        }

    }

    public void updateMintimeAverage(double min_throughput, double time) {
        if (sim_start_time > 0) {

            if (temp_min_throughput != 99999) {
                min_throughput_time = time - previous_min_time;
                min_thr_time_avg += temp_min_throughput * min_throughput_time;
                temp_min_throughput = min_throughput;
                previous_min_time = time;
            } else {
                non_user_time += time - previous_min_time;
                temp_min_throughput = min_throughput;
                previous_min_time = time;
            }

        } else {
            temp_min_throughput = min_throughput;
        }

    }

    public void updateMintimeAverageWiFi(double min_throughput, double time) {
        if (sim_start_time > 0) {

            if (temp_min_throughput_wifi != 99999) {
                min_throughput_time_wifi = time - previous_min_time_wifi;
                min_thr_time_avg_wifi += temp_min_throughput_wifi * min_throughput_time_wifi;
                temp_min_throughput_wifi = min_throughput;
                previous_min_time_wifi = time;
            } else {
                non_user_time_wifi += time - previous_min_time_wifi;
                temp_min_throughput_wifi = min_throughput;
                previous_min_time_wifi = time;
            }

        } else {
            temp_min_throughput_wifi = min_throughput;
        }

    }

    public void updateMintimeAverageLTEU(double min_throughput, double time) {//System.out.println(min_throughput);
        if (sim_start_time > 0) {

            if (temp_min_throughput_lteu != 99999) {
                min_throughput_time_lteu = time - previous_min_time_lteu;
                min_thr_time_avg_lteu += temp_min_throughput_lteu * min_throughput_time_lteu;
                temp_min_throughput_lteu = min_throughput;
                previous_min_time_lteu = time;
            } else {
                non_user_time_lteu += time - previous_min_time_lteu;
                previous_min_time_lteu = time;
                temp_min_throughput_lteu = min_throughput;
            }

        } else {
            temp_min_throughput_lteu = min_throughput;
        }

    }

    public void lte_wifi_num_count(UserNode un) throws IOException {
        if (un.getConnectedAP().ap_id < 10000) {

            FileWriter fw = new FileWriter("WiFi-224.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            String text;
            text = wifi_num + "," + un.getConnectedAP().ap_id + "," + un.getConnectedAP().max_capacity + "," + un.getConnectedAP().user_throughput + "," + un.getConnectedAP().assigned_channel +  "," + un.getArea() + "\n"; //un.getConnectedAP().located_area_id_id

            bw.write(text);
            bw.close();
            wifi_num++;
        } else {
            FileWriter fw1 = new FileWriter("NRU-224.csv", true);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            String text1;
            text1 = lte_num + "," + un.getConnectedAP().ap_id + "," + un.getConnectedAP().max_capacity + "," + un.getConnectedAP().user_throughput + "," + un.getConnectedAP().assigned_channel + "," + un.getArea() + "\n";

            bw1.write(text1);
            bw1.close();

            lte_num++;
        }

    }

    /* Processing at the end of one loop */
    public void loopEnd() {

        fairness_index_avg = Math.pow(ave_throughput, 2) / (target_connected_num * thr_2_jo_avg);
        fairness_index_avg_wifi = Math.pow(ave_throughput_wifi, 2) / (target_wifi_connected_num * thr_2_jo_avg_wifi);
        fairness_index_avg_lteu = Math.pow(ave_throughput_lteu, 2) / (target_lteu_connected_num * thr_2_jo_avg_lteu);

        ave_throughput = ave_throughput / target_connected_num;
        ave_transtime = ave_transtime / target_connected_num;

        ave_throughput_wifi = ave_throughput_wifi / target_wifi_connected_num;
        ave_transtime_wifi = ave_transtime_wifi / target_wifi_connected_num;

        ave_throughput_lteu = ave_throughput_lteu / target_lteu_connected_num;
        ave_transtime_lteu = ave_transtime_lteu / target_lteu_connected_num;

        avg_provided_data_size = avg_provided_data_size / target_connected_num;
        avg_provided_data_size_wifi = avg_provided_data_size_wifi / target_wifi_connected_num;
        avg_provided_data_size_lteu = avg_provided_data_size_lteu / target_lteu_connected_num;

        variance_per_user = variance_per_user / target_connected_num;
        variance_per_user_wifi = variance_per_user_wifi / target_wifi_connected_num;
        variance_per_user_lteu = variance_per_user_lteu / target_lteu_connected_num;
//
        min_thr_time_avg = min_thr_time_avg / ((end_time - sim_start_time) - non_user_time);
        min_thr_time_avg_wifi = min_thr_time_avg_wifi / ((end_time - sim_start_time) - non_user_time_wifi);
        min_thr_time_avg_lteu = min_thr_time_avg_lteu / ((end_time - sim_start_time) - non_user_time_lteu);

        double sum = 0;
        double sum_wifi = 0;
        double sum_lteu = 0;

        for (int i = 0; i < wifi_users_throghput.length; i++) {
            sum += wifi_users_throghput[i];
            sum_wifi += wifi_users_throghput[i];
        }

        for (int i = 0; i < lteu_users_throghput.length; i++) {
            sum += lteu_users_throghput[i];
            sum_lteu += lteu_users_throghput[i];
        }

        sum = sum / (wifi_users_throghput.length + lteu_users_throghput.length);
        sum_wifi = sum_wifi / wifi_users_throghput.length;
        sum_lteu = sum_lteu / lteu_users_throghput.length;

        for (int i = 0; i < wifi_users_throghput.length; i++) {
            variance += (wifi_users_throghput[i] - sum) * (wifi_users_throghput[i] - sum);
            variance_wifi += (wifi_users_throghput[i] - sum_wifi) * (wifi_users_throghput[i] - sum_wifi);
        }

        for (int i = 0; i < lteu_users_throghput.length; i++) {
            variance += (lteu_users_throghput[i] - sum) * (lteu_users_throghput[i] - sum);
            variance_lteu += (lteu_users_throghput[i] - sum_lteu) * (lteu_users_throghput[i] - sum_lteu);
        }

        variance = variance / (wifi_users_throghput.length + lteu_users_throghput.length);
        variance_wifi = variance_wifi / wifi_users_throghput.length;
        variance_lteu = variance_lteu / lteu_users_throghput.length;

        fairness_index_min = Math.pow(fairness_index_min, 2) / (target_connected_num * thr_2_jo_min);
        fairness_index_min_wifi = Math.pow(fairness_index_min_wifi, 2) / (target_wifi_connected_num * thr_2_jo_min_wifi);
        fairness_index_min_lteu = Math.pow(fairness_index_min_lteu, 2) / (target_lteu_connected_num * thr_2_jo_min_lteu);
//        System.out.println(wifi_num +"\t"+lte_num);
    }

    public void updateEndTime(double time) {
        end_time = time;
    }

    public DataOfOneSim(UserParameter param) {
        ave_throughput = 0.0;
        ave_transtime = 0.0;
        target_end_num = 0;
        target_connected_num = 0;
        end_time = 0;

        target_wifi_end_num = 0;
        target_lteu_end_num = 0;
        target_wifi_connected_num = 0;
        target_lteu_connected_num = 0;

        ave_throughput_wifi = 0.0;
        ave_transtime_wifi = 0.0;

        ave_throughput_lteu = 0.0;
        ave_transtime_lteu = 0.0;

        min_throughput = 999999;
        min_trans_time = 999999;
        min_wifi_throughput = 999999;
        min_wifi_trans_time = 999999;
        min_lteu_throughput = 999999;
        min_lteu_trans_time = 999999;

        max_throughput = 0.0;
        max_trans_time = 0.0;
        max_wifi_throughput = 0.0;
        max_wifi_trans_time = 0.0;
        max_lteu_throughput = 0.0;
        max_lteu_trans_time = 0.0;

        avg_provided_data_size = 0.0;
        avg_provided_data_size_wifi = 0.0;
        avg_provided_data_size_lteu = 0.0;

        variance_per_user = 0;
        variance_per_user_wifi = 0;
        variance_per_user_lteu = 0;

        variance = 0.0;
        variance_wifi = 0.0;
        variance_lteu = 0.0;

        wifi_users_throghput = new double[param.end_num];
        lteu_users_throghput = new double[param.end_num];
        wifi_users_min_throghput = new double[param.end_num];
        lteu_users_min_throghput = new double[param.end_num];
        wifi_users_min_time_throghput = new double[param.end_num];
        lteu_users_min_time_throghput = new double[param.end_num];

        min_thr_time_avg = 0.0;
        min_thr_time_avg_wifi = 0.0;
        min_thr_time_avg_lteu = 0.0;

        min_throughput_time = 0;
        min_throughput_time_wifi = 0;
        min_throughput_time_lteu = 0;

        previous_min_time = 0;
        previous_min_time_wifi = 0;
        previous_min_time_lteu = 0;

        temp_min_throughput = 99999;
        temp_min_throughput_wifi = 99999;
        temp_min_throughput_lteu = 99999;

        non_user_time = 0.0;
        non_user_time_wifi = 0.0;
        non_user_time_lteu = 0.0;

        fairness_index_avg = 0.0;
        fairness_index_avg_wifi = 0.0;
        fairness_index_avg_lteu = 0.0;

        thr_2_jo_avg = 0.0;
        thr_2_jo_avg_wifi = 0.0;
        thr_2_jo_avg_lteu = 0.0;

        fairness_index_min = 0.0;
        fairness_index_min_wifi = 0.0;
        fairness_index_min_lteu = 0.0;

        thr_2_jo_min = 0.0;
        thr_2_jo_min_wifi = 0.0;
        thr_2_jo_min_lteu = 0.0;

        sim_start_time = 0;

        wifi_num = 0;
        lte_num = 0;

    }
}
