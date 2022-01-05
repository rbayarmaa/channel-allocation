/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * シミュレーション結果出力用クラス
 *
 * @author ginnan
 */
public class Output {

    UserParameter _param;

    //ネットワーク全体
    private double loop_average_throughput; //平均スループット
    private double loop_average_transmission; //平均通信時間
    private double loop_connected_num; //接続数の総計    
    private double loop_sim_time; //シミュレーション時間

    //WiFiのみユーザ
    private double loop_average_throughput_wifi;
    private double loop_average_transtime_wifi;
    private double loop_target_wifi_connected_num;

    //WiFi + LTE-Uユーザ
    private double loop_average_throughput_lteu;
    private double loop_average_transtime_lteu;
    private double loop_target_lteu_connected_num;

    //最小値の統計
    //全体
    private double loop_min_throughput; //平均スループット
    private double loop_min_transmission; //平均通信時間  

    //WiFiユーザ
    private double loop_min_throughput_wifi;
    private double loop_min_transtime_wifi;

    //LTE+WiFiユーザ
    private double loop_min_throughput_lteu;
    private double loop_min_transtime_lteu;

    //最大値の統計
    private double loop_max_throughput; //平均スループット
    private double loop_max_transmission; //平均通信時間  

    private double loop_max_throughput_wifi;
    private double loop_max_transtime_wifi;

    private double loop_max_throughput_lteu;
    private double loop_max_transtime_lteu;

    //提供できたデータ量(一定時間通信の場合)(全体、WiFiユーザ, LTE+WiFiユーザ)
    private double loop_average_provided_data_size;
    private double loop_average_provided_data_size_wifi;
    private double loop_average_provided_data_size_lteu;

    //ユーザが得られたスループットの分散(全体、WiFiユーザ, LTE+WiFiユーザ)
    private double loop_variance_per_user;
    private double loop_variance_per_user_wifi;
    private double loop_variance_per_user_lteu;

    //平均スループットの分散(全体、WiFiユーザ, LTE+WiFiユーザ)→こっちの分散が使っているほう
    private double loop_variance;
    private double loop_variance_wifi;
    private double loop_variance_lteu;

    //最小スループットの時間平均(全体, WiFi AP, LTE BS)
    private double loop_min_thr_time_avg;
    private double loop_min_thr_time_avg_wifi;
    private double loop_min_thr_time_avg_lteu;

    //LTE-UとWiFiユーザのうち、どちらにつないだユーザが多いか
    private double loop_wifi_num;
    private double loop_lte_num;

    //fairness index(平均スループット)
    private double loop_fairness_index_avg;
    private double loop_fairness_index_avg_wifi;
    private double loop_fairness_index_avg_lteu;

    //fairness index(最小スループット)
    private double loop_fairness_index_min;
    private double loop_fairness_index_min_wifi;
    private double loop_fairness_index_min_lteu;

    int loop;

    private String directory_name = "";

    /* 1ループごとの性能評価値を更新 */
    public void update(Scenario _scenario) throws IOException {
        DataOfOneSim _data = _scenario.getData();

        //ネットワーク全体
        loop_average_throughput += _data.ave_throughput;
        loop_average_transmission += _data.ave_transtime;
        loop_connected_num += _data.target_connected_num;
        loop_sim_time += _data.end_time;

        //WiFiのみユーザ
        loop_average_throughput_wifi += _data.ave_throughput_wifi;
        loop_average_transtime_wifi += _data.ave_transtime_wifi;
        loop_target_wifi_connected_num += _data.target_wifi_connected_num;

        //WiFi + LTE-Uユーザ
        loop_average_throughput_lteu += _data.ave_throughput_lteu;
        loop_average_transtime_lteu += _data.ave_transtime_lteu;
        loop_target_lteu_connected_num += _data.target_lteu_connected_num;

        //最小値の処理   
        loop_min_throughput += _data.min_throughput;
        loop_min_transmission += _data.min_trans_time;

        loop_min_throughput_wifi += _data.min_wifi_throughput;
        loop_min_transtime_wifi += _data.min_wifi_trans_time;

        loop_min_throughput_lteu += _data.min_lteu_throughput;
        loop_min_transtime_lteu += _data.min_lteu_trans_time;


//        writeToFile_min_thr_perSimu(_data.min_throughput, _data.min_wifi_throughput, _data.min_lteu_throughput);
        //最大値の処理   
        loop_max_throughput += _data.max_throughput;
        loop_max_transmission += _data.max_trans_time;

        loop_max_throughput_wifi += _data.max_wifi_throughput;
        loop_max_transtime_wifi += _data.max_wifi_trans_time;

        loop_max_throughput_lteu += _data.max_lteu_throughput;
        loop_max_transtime_lteu += _data.max_lteu_trans_time;

        //一定時間通信の処理
        loop_average_provided_data_size += _data.avg_provided_data_size;
        loop_average_provided_data_size_wifi += _data.avg_provided_data_size_wifi;
        loop_average_provided_data_size_lteu += _data.avg_provided_data_size_lteu;

        //分散の処理
        loop_variance_per_user += _data.variance_per_user;
        loop_variance_per_user_wifi += _data.variance_per_user_wifi;
        loop_variance_per_user_lteu += _data.variance_per_user_lteu;

        //分散の処理(平均スループット)
        loop_variance += _data.variance;
        loop_variance_wifi += _data.variance_wifi;
        loop_variance_lteu += _data.variance_lteu;

        //最小スループットの時間平均
        loop_min_thr_time_avg += _data.min_thr_time_avg;
        loop_min_thr_time_avg_wifi += _data.min_thr_time_avg_wifi;
        loop_min_thr_time_avg_lteu += _data.min_thr_time_avg_lteu;

        loop_wifi_num += _data.wifi_num;
        loop_lte_num += _data.lte_num;

        //fairness index(平均スループット)
        loop_fairness_index_avg += _data.fairness_index_avg;
        loop_fairness_index_avg_wifi += _data.fairness_index_avg_wifi;
        loop_fairness_index_avg_lteu += _data.fairness_index_avg_lteu;
        //fairness index(最小スループット)
        loop_fairness_index_min += _data.fairness_index_min;
        loop_fairness_index_min_wifi += _data.fairness_index_min_wifi;
        loop_fairness_index_min_lteu += _data.fairness_index_min_lteu;

        //ループごとのシミュレーション結果を取る場合(不要な時は消すことを推奨)
        writePerSimuData(_data);
    }

    /* 指定ループ回シミレーション終了後、性能評価値を算出 */
    public void executeSimEnd() {
        int loop_num = _param.loop_num;

        //ネットワーク全体
        loop_average_throughput = loop_average_throughput / loop_num;
        loop_average_transmission = loop_average_transmission / loop_num;
        loop_connected_num = loop_connected_num / loop_num;
        loop_sim_time = loop_sim_time / loop_num;

        //WiFiのみユーザ
        loop_average_throughput_wifi = loop_average_throughput_wifi / loop_num;
        loop_average_transtime_wifi = loop_average_transtime_wifi / loop_num;
        loop_target_wifi_connected_num = loop_target_wifi_connected_num / loop_num;

        //WiFi + LTE-Uユーザ
        loop_average_throughput_lteu = loop_average_throughput_lteu / loop_num;
        loop_average_transtime_lteu = loop_average_transtime_lteu / loop_num;
        loop_target_lteu_connected_num = loop_target_lteu_connected_num / loop_num;

        //最小値の処理
        loop_min_throughput = loop_min_throughput / loop_num;
        loop_min_transmission = loop_min_transmission / loop_num;

        loop_min_throughput_wifi = loop_min_throughput_wifi / loop_num;
        loop_min_transtime_wifi = loop_min_transtime_wifi / loop_num;

        loop_min_throughput_lteu = loop_min_throughput_lteu / loop_num;
        loop_min_transtime_lteu = loop_min_transtime_lteu / loop_num;

        //最大値の処理
        loop_max_throughput = loop_max_throughput / loop_num;
        loop_max_transmission = loop_max_transmission / loop_num;

        loop_max_throughput_wifi = loop_max_throughput_wifi / loop_num;
        loop_max_transtime_wifi = loop_max_transtime_wifi / loop_num;

        loop_max_throughput_lteu = loop_max_throughput_lteu / loop_num;
        loop_max_transtime_lteu = loop_max_transtime_lteu / loop_num;

        //一定時間通信の処理
        loop_average_provided_data_size = loop_average_provided_data_size / loop_num;
        loop_average_provided_data_size_wifi = loop_average_provided_data_size_wifi / loop_num;
        loop_average_provided_data_size_lteu = loop_average_provided_data_size_lteu / loop_num;

        //分散の処理
        loop_variance_per_user = loop_variance_per_user / loop_num;
        loop_variance_per_user_wifi = loop_variance_per_user_wifi / loop_num;
        loop_variance_per_user_lteu = loop_variance_per_user_lteu / loop_num;

        //分散の処理(平均スループット)
        loop_variance = loop_variance / loop_num;
        loop_variance_wifi = loop_variance_wifi / loop_num;
        loop_variance_lteu = loop_variance_lteu / loop_num;

        //最小スループットの時間平均
        loop_min_thr_time_avg = loop_min_thr_time_avg / loop_num;
        loop_min_thr_time_avg_wifi = loop_min_thr_time_avg_wifi / loop_num;
        loop_min_thr_time_avg_lteu = loop_min_thr_time_avg_lteu / loop_num;

        //fairness index(平均スループット)
        loop_fairness_index_avg = loop_fairness_index_avg / loop_num;
        loop_fairness_index_avg_wifi = loop_fairness_index_avg_wifi / loop_num;
        loop_fairness_index_avg_lteu = loop_fairness_index_avg_lteu / loop_num;

        //fairness index(最小スループット)
        loop_fairness_index_min = loop_fairness_index_min / loop_num;
        loop_fairness_index_min_wifi = loop_fairness_index_min_wifi / loop_num;
        loop_fairness_index_min_lteu = loop_fairness_index_min_lteu / loop_num;

        loop_wifi_num = loop_wifi_num / loop_num;
        loop_lte_num = loop_lte_num / loop_num;

    }

    /*ファイル出力関数:ファイルダウンロード用*/
    public void writeToFile() throws IOException {
        FileWriter fw = new FileWriter(directory_name + Constants.VERSION + ".dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        String text_all = _param.loop_num + "\t"
                + Constants.GA_NUM + "\t"
                + Constants.LTEU_NUM + "\t"
                + _param.wifi_user_lambda + "\t"
                + _param.lteu_user_lambda + "\t"
                + _param.interval_time + "\t"
                + _param.service_set + "\t"
                + Constants.CHANNEL_NUM + "\t"
                + _param.end_num + "\t"
                + loop_average_throughput + "\t"
                + loop_average_transmission + "\t"
                + loop_connected_num + "\t";

        String text_wifi = loop_average_throughput_wifi + "\t"
                + loop_average_transtime_wifi + "\t"
                + loop_target_wifi_connected_num + "\t";

        String text_lteu = loop_average_throughput_lteu + "\t"
                + loop_average_transtime_lteu + "\t"
                + loop_target_lteu_connected_num + "\t";

        String text_min = loop_min_throughput + "\t"
                + loop_min_transmission + "\t"
                + loop_min_throughput_wifi + "\t"
                + loop_min_transtime_wifi + "\t"
                + loop_min_throughput_lteu + "\t"
                + loop_min_transtime_lteu + "\t";

        String text_max = loop_max_throughput + "\t"
                + loop_max_transmission + "\t"
                + loop_max_throughput_wifi + "\t"
                + loop_max_transtime_wifi + "\t"
                + loop_max_throughput_lteu + "\t"
                + loop_max_transtime_lteu + "\t";

        String text_variance = loop_variance_per_user + "\t"
                + loop_variance_per_user_wifi + "\t"
                + loop_variance_per_user_lteu + "\t";

        String text_variance2 = loop_variance + "\t"
                + loop_variance_wifi + "\t"
                + loop_variance_lteu + "\t";

        bw.write(text_all);
        bw.write(text_wifi);
        bw.write(text_lteu);
        bw.write("min_values" + "\t" + text_min);
        bw.write("max_values" + "\t" + text_max);
        bw.write("variance" + "\t" + text_variance);
        bw.write("variance2" + "\t" + text_variance2);

        bw.write(loop_sim_time + "\n");
        bw.close();

    }

    /*ファイル出力関数:一定時間通信用*/
    public void writeToFile2() throws IOException {
        FileWriter fw = new FileWriter(directory_name + "180_second_2.dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        String text_all = _param.loop_num + "\t"
                + _param.ga_loop_num + "\t"
                + _param.elite_num + "\t"
                + _param.crossover_parent_num + "\t"
                + _param.mutation_prob + "\t"
                + _param.ga_individual_num + "\t"
                + Constants.LTEU_NUM + "\t"
                + _param.wifi_user_lambda + "\t"
                + _param.lteu_user_lambda + "\t"
                + _param.interval_time + "\t"
                + Constants.WiFi_NUM + "\t"
                + Constants.CHANNEL_NUM + "\t"
                + "mu" + Constants.mu + "\t"
                + _param.end_num + "\t"
                + loop_average_throughput + "\t"
                + loop_average_transmission + "\t"
                + loop_connected_num + "\t";

        String text_wifi = loop_average_throughput_wifi + "\t"
                + loop_target_wifi_connected_num + "\t";

        String text_lteu = loop_average_throughput_lteu + "\t"
                + loop_target_lteu_connected_num + "\t";

        String text_min = loop_min_throughput + "\t"
                + loop_min_throughput_wifi + "\t"
                + loop_min_throughput_lteu + "\t";

        String text_data = loop_average_provided_data_size + "\t"
                + loop_average_provided_data_size_wifi + "\t"
                + loop_average_provided_data_size_lteu + "\t";

        String text_variance = loop_variance_per_user + "\t"
                + loop_variance_per_user_wifi + "\t"
                + loop_variance_per_user_lteu + "\t";

        String text_variance2 = loop_variance + "\t"
                + loop_variance_wifi + "\t"
                + loop_variance_lteu + "\t";

        String text_min_time_thr = loop_min_thr_time_avg + "\t"
                + loop_min_thr_time_avg_wifi + "\t"
                + loop_min_thr_time_avg_lteu + "\t";

        String fair_avg = loop_fairness_index_avg + "\t"
                + loop_fairness_index_avg_wifi + "\t"
                + loop_fairness_index_avg_lteu + "\t";

        String fair_min = loop_fairness_index_min + "\t"
                + loop_fairness_index_min_wifi + "\t"
                + loop_fairness_index_min_lteu + "\t";

        String numunm = loop_wifi_num + "\t"
                + loop_lte_num + "\t";

        bw.write(text_all);
        bw.write(text_wifi);
        bw.write(text_lteu);
        bw.write("min_values" + "\t" + text_min);
        bw.write("data_size" + "\t" + text_data);
        bw.write("variance" + "\t" + text_variance);
        bw.write("variance2" + "\t" + text_variance2);
        bw.write("min_time_thr" + "\t" + text_min_time_thr);
        bw.write("fair_avg" + "\t" + fair_avg);
        bw.write("fair_min" + "\t" + fair_min);
        bw.write("numnum" + "\t" + numunm);

        bw.write(loop_sim_time + "\t" + Constants.GA_INDIVIDUAL_NUM + "\n");
        bw.close();

    }

    /* 計測時間をファイルに出力*/
    public void writeToFile_SimuTime(long time) throws IOException {
        FileWriter fw = new FileWriter(directory_name + "time.dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        String text = "NEW" + "\t" + _param.loop_num + "\t" + _param.ga_loop_num + "\t" + _param.wifi_user_lambda + "\t" + time + "[ms]" + "\n";
        bw.write(text);
        bw.close();
    }

    /* ユーザスループットをファイル出力(おそらく、全ユーザのスループットを取得するためのもの)*/
    public void writeToFile_Throughput(double[] wifi, double[] lteu, double[] wifi_min, double[] lteu_min) throws IOException {
        FileWriter fw = new FileWriter(directory_name + "thorughput.dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("WiFi" + "\t" + "LTE-U" + "\t" + "WiFi_MIN" + "\t" + "LTE-U_MIN" + "\n");
        String text;

        for (int i = 0; i < wifi.length; i++) {
            text = wifi[i] + "\t" + lteu[i] + "\t" + wifi_min[i] + "\t" + lteu_min[i] + "\n";
            bw.write(text);
        }

        bw.close();
    }

    /* ユーザスループットをファイル出力(おそらく、全ユーザのスループットを取得するためのもの)*/
    public void writeToFile_Throughput_time_Avg(double[] wifi, double[] lteu) throws IOException {
        FileWriter fw = new FileWriter(directory_name + "time_avg_thorughput.dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("WiFi" + "\t" + "LTE-U" + "\n");
        String text;

        for (int i = 0; i < wifi.length; i++) {
            text = wifi[i] + "\t" + lteu[i] + "\n";
            bw.write(text);
        }

        bw.close();
    }

    /* シミュレーションごとの最小スループットを記録 */
    public void writeToFile_min_thr_perSimu(double min_thr, double min_wifi, double min_lteu) throws IOException {
        FileWriter fw = new FileWriter(directory_name + "per_simu_min.dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        loop++;
        String text;

        text = loop + "\t" + min_thr + "\t" + min_wifi + "\t" + min_lteu + "\n";
        bw.write(text);

        bw.close();
    }

    /* シミュレーションごとのデータを記述⇒平均はあとで自分でやる */
    private void writePerSimuData(DataOfOneSim dt) throws IOException {
        FileWriter fw = new FileWriter(directory_name + "data.dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        loop++;

        if (loop == 1) {
            String text = "Loop" + "\t" + "|Avg" + "\t" + "wifi" + "\t" + "lteu|" + "\t"
                    + "|MinThr" + "\t" + "wifi" + "\t" + "lteu|" + "\t"
                    + "|vari2" + "\t" + "wifi" + "\t" + "lteu|" + "\t"
                    + "|MinThrtime" + "\t" + "wifi" + "\t" + "lteu|" + "\t" + "\n";
            bw.write(text);
        }

        //平均スループット
        String avg_thr = +loop + "\t"
                + dt.ave_throughput + "\t"
                + dt.ave_throughput_lteu + "\t"
                + dt.ave_throughput_wifi + "\t";

        //最小スループット
        String min_thr = dt.min_throughput + "\t"
                + dt.min_wifi_throughput + "\t"
                + dt.min_lteu_throughput + "\t";

        //スループットの分散
        String vari2 = dt.variance + "\t"
                + dt.variance_wifi + "\t"
                + dt.variance_lteu + "\t";

        //最小スループットの時間平均
        String min_thr_time = dt.min_thr_time_avg + "\t"
                + dt.min_thr_time_avg_wifi + "\t"
                + dt.min_thr_time_avg_lteu + "\t";

        //平均スループットのfairness index
        String fair_avg = dt.fairness_index_avg + "\t"
                + dt.fairness_index_avg_wifi + "\t"
                + dt.fairness_index_avg_lteu + "\t";

        //最小スループットのfairness indez
        String fair_min = dt.fairness_index_min + "\t"
                + dt.fairness_index_min_wifi + "\t"
                + dt.fairness_index_min_lteu + "\t" + "\n";

        bw.write(avg_thr);
        bw.write(min_thr);
        bw.write(vari2);
        bw.write(min_thr_time);
        bw.write(fair_avg);
        bw.write(fair_min);

        bw.close();
    }


    /* 画面出力関数:ファイルダウンロード用 */
    public void printToScreen() {
        System.out.println();
        System.out.println("\t平均スループット[Mbps]\t平均通信時間[s]\t\t終了呼数\t");
        System.out.println("ALL\t" + loop_average_throughput + "\t" + loop_average_transmission + "\t" + loop_connected_num + "\t");
        System.out.println("wifi\t" + loop_average_throughput_wifi + "\t" + loop_average_transtime_wifi + "\t" + loop_target_wifi_connected_num + "\t");
        System.out.println("lte-u\t" + loop_average_throughput_lteu + "\t" + loop_average_transtime_lteu + "\t" + loop_target_lteu_connected_num + "\t");
        System.out.println();
        System.out.println("最小値");
        System.out.println("\t平均スループット[Mbps]\t平均通信時間[s]\t");
        System.out.println("ALL\t" + loop_min_throughput + "\t" + loop_min_transmission + "\t");
        System.out.println("wifi\t" + loop_min_throughput_wifi + "\t" + loop_min_transtime_wifi + "\t");
        System.out.println("lte-u\t" + loop_min_throughput_lteu + "\t" + loop_min_transtime_lteu + "\t");
        System.out.println();
        System.out.println("最大値");
        System.out.println("\t平均スループット[Mbps]\t平均通信時間[s]\t");
        System.out.println("ALL\t" + loop_max_throughput + "\t" + loop_max_transmission + "\t");
        System.out.println("wifi\t" + loop_max_throughput_wifi + "\t" + loop_max_transtime_wifi + "\t");
        System.out.println("lte-u\t" + loop_max_throughput_lteu + "\t" + loop_max_transtime_lteu + "\t");
        System.out.println();
        System.out.println("ユーザごとの分散");
        System.out.println("\tALL\twifi\tlte-u\t");
        System.out.println("\t" + loop_variance_per_user + "\t" + loop_variance_per_user_wifi + "\t" + loop_variance_per_user_lteu + "\t");
        System.out.println();
        System.out.println("全ユーザの分散");
        System.out.println("\tALL\twifi\tlte-u\t");
        System.out.println("\t" + loop_variance + "\t" + loop_variance_wifi + "\t" + loop_variance_lteu + "\t");

    }

    /* 画面出力関数 :一定時間通信用*/
    public void printToScreen2() {
        System.out.println();
        System.out.println("平均通信時間[s]:\t" + loop_average_transmission);
        System.out.println("\t平均スループット[Mbps]\t提供データサイズ[Mbit]\t\t終了呼数\t");
        System.out.println("ALL\t" + loop_average_throughput + "\t" + loop_average_provided_data_size + "\t" + loop_connected_num + "\t");
        System.out.println("wifi\t" + loop_average_throughput_wifi + "\t" + loop_average_provided_data_size_wifi + "\t" + loop_target_wifi_connected_num + "\t");
        System.out.println("lte-u\t" + loop_average_throughput_lteu + "\t" + loop_average_provided_data_size_lteu + "\t" + loop_target_lteu_connected_num + "\t");
        System.out.println();
        System.out.println("最小値");
        System.out.println("\t平均スループット[Mbps]\t");
        System.out.println("ALL\t" + loop_min_throughput + "\t");
        System.out.println("wifi\t" + loop_min_throughput_wifi + "\t");
        System.out.println("lte-u\t" + loop_min_throughput_lteu + "\t");
        System.out.println("ユーザごとの分散");
        System.out.println("\tALL\twifi\tlte-u\t");
        System.out.println("\t" + loop_variance_per_user + "\t" + loop_variance_per_user_wifi + "\t" + loop_variance_per_user_lteu + "\t");
        System.out.println();
        System.out.println("全ユーザの分散");
        System.out.println("\tALL\twifi\tlte-u\t");
        System.out.println("\t" + loop_variance + "\t" + loop_variance_wifi + "\t" + loop_variance_lteu + "\t");
        System.out.println();
        System.out.println("最小スループットの時間平均の平均");
        System.out.println("\tALL\twifi\tlte-u\t");
        System.out.println("\t" + loop_min_thr_time_avg + "\t" + loop_min_thr_time_avg_wifi + "\t" + loop_min_thr_time_avg_lteu + "\t");
        System.out.println();
        System.out.println("Fairness index");
        System.out.println("\tALL\twifi\tlte-u\t");
        System.out.println("平均" + "\t" + loop_fairness_index_avg + "\t" + loop_fairness_index_avg_wifi + "\t" + loop_fairness_index_avg_lteu + "\t");
        System.out.println("最小" + "\t" + loop_fairness_index_min + "\t" + loop_fairness_index_min_wifi + "\t" + loop_fairness_index_min_lteu + "\t");
        System.out.println();
        System.out.println("LTE-UとWiFiどちらに接続した?" + "\t" + "WiFi:" + loop_wifi_num + "\t" + "LTE-U:" + loop_lte_num);

    }

    private void Init() throws IOException {
        //ネットワーク全体
        loop_average_throughput = 0.0;
        loop_average_transmission = 0.0;
        loop_connected_num = 0;
        loop_sim_time = 0.0;

        //WiFiのみユーザ
        loop_average_throughput_wifi = 0.0;
        loop_average_transtime_wifi = 0.0;
        loop_target_wifi_connected_num = 0;

        //WiFi + LTE-Uユーザ
        loop_average_throughput_lteu = 0.0;
        loop_average_transtime_lteu = 0.0;
        loop_target_lteu_connected_num = 0;

        //最小値の処理
        loop_min_throughput = 0.0;
        loop_min_transmission = 0.0;

        loop_min_throughput_wifi = 0.0;
        loop_min_transtime_wifi = 0.0;

        loop_min_throughput_lteu = 0.0;
        loop_min_transtime_lteu = 0.0;

        //最大値の処理
        loop_max_throughput = 0.0;
        loop_max_transmission = 0.0;

        loop_max_throughput_wifi = 0.0;
        loop_max_transtime_wifi = 0.0;

        loop_max_throughput_lteu = 0.0;
        loop_max_transtime_lteu = 0.0;

        //一定時間通信する場合の処理
        loop_average_provided_data_size = 0.0;
        loop_average_provided_data_size_wifi = 0.0;
        loop_average_provided_data_size_lteu = 0.0;

        //分散の処理
        loop_variance_per_user = 0.0;
        loop_variance_per_user_wifi = 0.0;
        loop_variance_per_user_lteu = 0.0;

        //分散の処理(平均スループット)
        loop_variance = 0.0;
        loop_variance_wifi = 0.0;
        loop_variance_lteu = 0.0;

        loop_min_thr_time_avg = 0.0;
        loop_min_thr_time_avg_wifi = 0.0;
        loop_min_thr_time_avg_lteu = 0.0;

        //fairness index(平均スループット)
        loop_fairness_index_avg = 0.0;
        loop_fairness_index_avg_wifi = 0.0;
        loop_fairness_index_avg_lteu = 0.0;

        //fairness index(最小スループット)
        loop_fairness_index_min = 0.0;
        loop_fairness_index_min_wifi = 0.0;
        loop_fairness_index_min_lteu = 0.0;

        loop_wifi_num = 0.0;
        loop_lte_num = 0.0;

        //WriteToFile_min_thr_perSimu用
        loop = 0;

        //ディレクトリ作成
        directory_name = createDirectory(_param.select_method);

    }

    /* 結果ファイル格納ディレクトリを作成する。 */
    private String createDirectory(int select_method) throws IOException {
        String directory_name = "";
        switch (select_method) {
            case 1:
                directory_name = statInitialization("./WiFiLTEU_Data/ProposedMethod/");
                break;

            case 2:
                directory_name = statInitialization("./WiFiLTEU_Data/RandomAssign/");
                break;

            case 3:
                directory_name = statInitialization("./WiFiLTEU_Data/ChannelOff/");
                break;

            case 4:
                directory_name = statInitialization("./WiFiLTEU_Data/NeighborLeastChannel/");
                break;

            case 5:
                directory_name = statInitialization("./WiFiLTEU_Data/ProposedConnectionOnly/");
                break;

            case 6:
                directory_name = statInitialization("./WiFiLTEU_Data/BruteForce/");
                break;

            case 7:
                directory_name = statInitialization("./WiFiLTEU_Data/GA2/");
                break;

            case 8:
                directory_name = statInitialization("./WiFiLTEU_Data/GA_Avg/");
                break;

            case 9:
                directory_name = statInitialization("./WiFiLTEU_Data/GA_min_avg/");
                break;

            case 10:
                directory_name = statInitialization("./WiFiLTEU_Data/GA_10_min/");
                break;

            case 11:
                directory_name = statInitialization("./WiFiLTEU_Data/GA_11_avg/");
                break;

            case 12:
                directory_name = statInitialization("./WiFiLTEU_Data/CAonly/");
                break;

            case 13:
                directory_name = statInitialization("./WiFiLTEU_Data/GA_13_log/");
                break;

            case 14:
                directory_name = statInitialization("./WiFiLTEU_Data/GA_14_avg_fast/");
                break;

            case 15:
                directory_name = statInitialization("./WiFiLTEU_Data/DynamicAssign/");
                break;

            case 16:
                directory_name = statInitialization("./WiFiLTEU_Data/GA_16_Fair/");
                break;

            default:
                directory_name = statInitialization("./WiFiLTEU_Data/");
        }

        return directory_name;
    }

    /* 統計情報出力用のディレクトリを作成 */
    private String statInitialization(String directory_name) throws IOException {
        File data_directory = new File(directory_name);
        data_directory.mkdirs();
        return directory_name;
    }

    public Output(UserParameter param) throws IOException {
        _param = param;
        Init();
    }

}
