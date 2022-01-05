/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * ユーザ端末の情報
 *
 * @author ginnan
 */
public class UserNode {

    private final int node_id; // ユーザのID (たぶん使っていない

    private double assigned_throughput; //ユーザが得られるスループット

    private double start_time; //通信を開始する時間
    private double end_time; //通信を終了する時間

    /*一定時間通信する場合のパラメータ*/
    private double communication_time;  //通信時間 (たぶん使ってない気がする
    private double provided_data_size; //ユーザに提供できたデータ量
    private double average_throughput; //通信中の平均スループット
    private double thr_recalc_num; //スループットが再計算された回数
    private double min_user_throughput; //通信中の最小のスループット

    private double min_throughput_time; //通信時間のうち最小のスループットである時間
    private double min_time_average_thr; //最小スループットの時間平均

    /*The one that records the time when a certain throughput is obtained until the end of communication of the user*/
    private ArrayList<Double> communication_times;

    /*ファイルダウンロードをする場合のパラメータ*/
    private double previous_update_time;
    private double origin_file_size;
    private double current_file_size;

    /* ユーザが得られるスループットの分散を計算するためのパラメータ */
    private ArrayList<Double> throughputs;

    protected final int user_set; //0:WiFiのみユーザ, 1:LTE+WiFiユーザ
    protected int wifi_node_id;
    protected int lteu_node_id;

    private int area; //ユーザがいるエリア

    private AccessPoint connected_ap; //接続先AP or BS

    private LinkedList coverd_aps; //ユーザをカバーしているAPの集合

    private Event end_event; //通信終了のイベント

    public UserNode(int current_area, int node_id, double current_time, int system_set, Area _area, int system_id) {
        this.node_id = node_id;
        this.area = current_area;
        this.user_set = system_set;
        this.start_time = current_time;
        this.throughputs = new ArrayList<>();

        this.min_throughput_time = 0.0;

        this.communication_times = new ArrayList<>();
        //一定時間通信の場合のやつ 20171130 add
        this.average_throughput = 0.0;

        //system_set==0:WiFiユーザ, ststem_set==1:LTE+WiFiユーザ
        if (system_set == 0) {
            this.coverd_aps = _area.getAPcovered(area);//そのユーザをカバーしているAPをセット
            this.wifi_node_id = system_id;
        } else {
            this.coverd_aps = _area.getAPandBScovered(area);//そのユーザをカバーしているAP, BSをセット
            this.lteu_node_id = system_id;
        }

    }

    //Connection process when downloading files
    public void connect(double file_size, AccessPoint connected_ap) {
        this.connected_ap = connected_ap;

        origin_file_size = file_size;
        current_file_size = file_size;
        end_time = start_time + current_file_size / assigned_throughput;
        previous_update_time = start_time;

        throughputs.add(assigned_throughput);
    }

    //Connection processing when communicating for a certain period of time
    public void connect2(AccessPoint connected_ap, Scenario scenario) {
        this.connected_ap = connected_ap;
        end_time = start_time + Utility.expRand(Constants.mu, scenario.getRnd());
        communication_time = end_time - start_time;

        average_throughput += assigned_throughput;
        min_user_throughput = assigned_throughput;
        thr_recalc_num = 1;

        provided_data_size = 0.0;
        previous_update_time = start_time;

        throughputs.add(assigned_throughput);
    }

    public int getArea() {
        return area;
    }

    public LinkedList getCoveredAPs() {
        return coverd_aps;
    }

    public void setAssignedThroughput(double thr) {
        this.assigned_throughput = thr;
    }

    //* Recalculation of throughput of connected terminal in case of file download
    public void reCalcThroughput(double assign_throughput, double current_time) {
        double reduce_file_size = (current_time - previous_update_time) * this.assigned_throughput;
        current_file_size -= reduce_file_size;
        this.assigned_throughput = assign_throughput;

        if (current_time != previous_update_time) {
            communication_times.add(current_time - previous_update_time);
            throughputs.add(assigned_throughput);
        }

        previous_update_time = current_time;
        end_time = current_time + this.current_file_size / this.assigned_throughput;
        end_event.setTime(end_time);
    }

    // Recalculation of throughput of connected terminals during fixed-time communication
    public void reCalcThroughput2(double assign_throughput, double current_time) {

        provided_data_size += (current_time - previous_update_time) * this.assigned_throughput;

        this.assigned_throughput = assign_throughput;

        if (current_time != previous_update_time) {
            thr_recalc_num++;
            average_throughput += assigned_throughput;

            if (assigned_throughput < min_user_throughput) {
                min_user_throughput = assigned_throughput;

            }

            communication_times.add(current_time - previous_update_time);
            throughputs.add(assigned_throughput);
        }

        previous_update_time = current_time;

        end_event.setTime(end_time);

    }

    public void setEndEvent(Event end_event) {
        this.end_event = end_event;
    }

    public double getEndTime() {
        return end_time;
    }

    public AccessPoint getConnectedAP() {
        return connected_ap;
    }

    public void ChangeConnectedAP(AccessPoint ap) {
        connected_ap = ap;
    }

    //Get average throughput (for file downloads))
    public double getAverageThroughput() {
        return origin_file_size / (end_time - start_time);
    }

    public double getAverageThroughput(double time) {
        return (origin_file_size - current_file_size) / (time - start_time);
    }

    public double getTransTime() {
        return end_time - start_time;
    }

    public double getTransTime(double time) {
        return time - start_time + current_file_size / getAverageThroughput(time);
    }

    /*----- What to get when communicating for a certain period of time-------*/
    public void CalcAverageThroughput2() {
        average_throughput = average_throughput / thr_recalc_num;
    }

    public double getAverageThroughput2() {
//        return average_throughput;
        return provided_data_size / (end_time - start_time);
    }

    public double getMinThroughput() {
        return min_user_throughput;
    }

    /* Get the amount of data that could be provided: Processing when communicating for a certain period of time */
    public void CalcProvidedDataSize() {
        provided_data_size += (end_time - previous_update_time) * this.assigned_throughput;
    }

    public double getProvidedDataSize() {
        return provided_data_size;
    }

    public ArrayList getThroughputs() {
        return throughputs;
    }

    /* Calculate time average of minimum throughput * /// Probably not used
    public void CalcMinThroghputTimeAverage() {
        communication_times.add(end_time - previous_update_time);
        min_time_average_thr = 99999;
        for (int i = 0; i < communication_times.size(); i++) {

            if (throughputs.get(i) <= min_time_average_thr) {
                min_time_average_thr = throughputs.get(i);
                min_throughput_time = communication_times.get(i);

            } else if (throughputs.get(i) == min_time_average_thr) {
                min_throughput_time += communication_times.get(i);
            }
        }
        min_time_average_thr = min_time_average_thr * min_throughput_time / (end_time - start_time);

    }

    public double getMinThroghputTimeAverage() {
        return min_time_average_thr;
    }

    /* 最小スループットの時間平均を算出 *///おそらく使っていない
    public void CalcMinThroghputTime() {
        communication_times.add(end_time - previous_update_time);
        min_time_average_thr = 99999;
        min_throughput_time = 0;
        for (int i = 0; i < communication_times.size(); i++) {

            if (throughputs.get(i) <= min_time_average_thr) {
                min_time_average_thr = throughputs.get(i);
                min_throughput_time = communication_times.get(i);

            } else if (throughputs.get(i) == min_time_average_thr) {
                min_throughput_time += communication_times.get(i);
            }
        }

    }

    public double getMinThroghputTime() {
        return min_throughput_time;
    }
}
