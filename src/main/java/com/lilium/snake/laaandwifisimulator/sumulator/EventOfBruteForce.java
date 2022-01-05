/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * 総当たりでチャネル割り当て＆ユーザの接続先を探す
 * コードが間違っている気がする
 *
 * @author ginnan
 */
public class EventOfBruteForce extends Event {

    private final WiFiAP _wifi_ap[];
    private final LTEUBS _lteu_bs[];

    private int[][] max_pattern;    //評価値が最大のパターンを保存
    private double max_evaluation_value;    //最大の評価値
    int[][] channel_pattern = new int[Constants.WiFi_NUM][2];//WiFiのみチャネルを変更するため

    int count = 0;
    CheckInterference ci;

    public EventOfBruteForce(double time, Scenario scenario) {
        super(scenario);
        this.event_time = time;
        _wifi_ap = _area.getWiFiAP();
        _lteu_bs = _area.getLTEUBS();

        max_evaluation_value = 0;

        ci = new CheckInterference(_area);

    }

    @Override
    public void runEvent() throws IOException {
        startSearch();
    }

    private void startSearch() throws IOException {

        ArrayList<int[][]> patterns = new ArrayList<>();
//        int[][] channel_pattern = new int[Constants.WiFi_NUM][2];//WiFiのみチャネルを変更するため
        int wifi_id = 0;

        max_pattern = new int[Constants.WiFi_NUM][2];

        for (int ch = 0; ch < Constants.CHANNEL_NUM; ch++) {//System.out.println(ch);
            RecursionSearch(wifi_id, ch);
        }
//System.out.println("**");
        //最大の評価値の個体をBS,APに適用し、接続先を変更
//        for (int i = 0; i < Constants.LTEU_NUM; i++) {
//            _lteu_bs[i].assigned_channel = max_pattern[i][1];
//        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].assigned_channel = max_pattern[i][1];
        }

        //干渉の容量をセット
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].interference_list = ci.LTEUCheck(i, _wifi_ap, _lteu_bs);
            _lteu_bs[i].SetCapacity();
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].interference_list = ci.WiFiCheck(i, _wifi_ap, _lteu_bs);
            _wifi_ap[i].SetCapacity();
        }

        //ここで、ユーザの接続先選択をする
//        ProposedReConnect(max_pattern);
//System.out.println("2222");
        _area.CopyLTEUBS(_lteu_bs);
        _area.CopyWiFiAP(_wifi_ap);

        /*次の提案手法の発生イベントを作る*/
//        double time = _queue.getCurrentTime() + _param.interval_time;
//        Event next_event = new EventOfBruteForce(time, _scenario);
//        _queue.add(next_event);

    }

    private void RecursionSearch(int wifi_id, int channel) {//System.out.println("2222");
        double evaluation_value;
        channel_pattern[wifi_id][0] = wifi_id;
        channel_pattern[wifi_id][1] = channel;

        wifi_id++;
//System.out.print(wifi_id + "|"+ channel + " ");
        if (wifi_id < Constants.WiFi_NUM) {
            for (int ch = 0; ch < Constants.CHANNEL_NUM; ch++) {//System.out.println("\t" + wifi_id +"|"+ ch + "\t");
                RecursionSearch(wifi_id, ch); //System.out.print("\t");
//                System.out.println(wifi_id+"\t"+ch);
                count++;
            }
        }
//        System.out.println();
//System.out.println(wifi_id+"\t");
        evaluation_value = EvaluateSumCapacity(channel_pattern); //Evaluate(channel_pattern);

        if (max_evaluation_value < evaluation_value) {

            for (int i = 0; i < channel_pattern.length; i++) {
                System.arraycopy(channel_pattern[i], 0, max_pattern[i], 0, channel_pattern[i].length);

            }
//            max_pattern = channel_pattern;
        }
//System.out.println(count);
    }

    private void ProposedReConnect(int[][] individual) {

        //エリアごとのユーザ数を保持
        int[] area_wifi_user_num = new int[Constants.AREA_NUM];
        int[] area_lteu_user_num = new int[Constants.AREA_NUM];

        //エリアごとのUserNodeを保持
        LinkedList<UserNode>[] area_wifi_users = new LinkedList[Constants.AREA_NUM];
        LinkedList<UserNode>[] area_lteu_users = new LinkedList[Constants.AREA_NUM];

        for (int i = 0; i < Constants.AREA_NUM; i++) {
            area_wifi_users[i] = new LinkedList<>();
            area_lteu_users[i] = new LinkedList<>();
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            if (!_wifi_ap[i].UserList.isEmpty()) {
                for (int j = 0; j < _wifi_ap[i].UserList.size(); j++) {
                    if (_wifi_ap[i].UserList.get(j).user_set == 0) {
                        area_wifi_users[_wifi_ap[i].UserList.get(j).getArea()].add(_wifi_ap[i].UserList.get(j));
                        area_wifi_user_num[_wifi_ap[i].UserList.get(j).getArea()] += 1;
                    } else {
                        area_lteu_users[_wifi_ap[i].UserList.get(j).getArea()].add(_wifi_ap[i].UserList.get(j));
                        area_lteu_user_num[_wifi_ap[i].UserList.get(j).getArea()] += 1;
                    }
                }
            }
        }

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            if (!_lteu_bs[i].UserList.isEmpty()) {
                for (int j = 0; j < _lteu_bs[i].UserList.size(); j++) {
                    if (_lteu_bs[i].UserList.get(j).user_set == 1) {
                        area_lteu_users[_lteu_bs[i].UserList.get(j).getArea()].add(_lteu_bs[i].UserList.get(j));
                        area_lteu_user_num[_lteu_bs[i].UserList.get(j).getArea()] += 1;
                    } else {
                        System.out.println("LTE-u <- WiFi user ERROR");
                    }
                }
            }//System.out.println(i+"***"+_lteu_bs[i].UserList.size()+"\t" +  _lteu_bs[i].connecting_num);
        }

        //AP,BSのユーザ情報のクリア ＆個体のチャネル割り当てに更新
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].assigned_channel = individual[i][1];
            _wifi_ap[i].UserList.clear();
            _wifi_ap[i].connecting_num = 0;
        }

        for (int i = 0; i < Constants.LTEU_NUM; i++) {//System.out.println(i+"***"+_lteu_bs[i].UserList.size()+"\t" +  _lteu_bs[i].connecting_num);
            _lteu_bs[i].UserList.clear();
            _lteu_bs[i].connecting_num = 0;
        }

        //各エリアでどのAP,BSがそのエリアをカバーしているかのリスト
        ArrayList<Integer>[] ap_cover_set;
        ArrayList<Integer>[] bs_cover_set;

        ap_cover_set = _area.getAreaCooveredAPSet();
        bs_cover_set = _area.getAreaCooveredBSSet();

        //1. WiFi + LTEユーザを接続可能ならLTE-U BSに接続(BS同士はセルが重複していないと仮定)⇒【LTE-U BSが重複しててもいいように後で直す】
        for (int i = 0; i < Constants.AREA_NUM; i++) {
            if (bs_cover_set[i].size() != 0) {
                for (int k = 0; k < bs_cover_set[i].size(); k++) {      //System.out.println(area_lteu_user_num[i]+"**--*--"+ area_lteu_users[i].size());
                    while (!area_lteu_users[i].isEmpty()) {
                        _lteu_bs[bs_cover_set[i].get(k)].UserList.add(area_lteu_users[i].poll());
                    }                             // System.out.println(_lteu_bs[bs_cover_set[i].get(k)].UserList.size()+"\t" +area_lteu_users[i].size());
                    Utility.ChangeConnectingAPs(_lteu_bs[bs_cover_set[i].get(k)]);
                    _lteu_bs[bs_cover_set[i].get(k)].connecting_num += area_lteu_user_num[i];
                    _lteu_bs[bs_cover_set[i].get(k)].reconnect(event_time, _param.service_set);
                }
            }
        }

        PriorityQueue<APCover> aps_descender_area = new PriorityQueue<>(new EventOfGenericAlgorithm.MyComparator());
        //2.WiFiのみユーザ, LTE + WiFiユーザの接続先WiFi APを決定
        //接続先候補の少ないユーザ順に並べる⇒カバーAP数の少ないエリア順に並べることと等価
        APCover ap_cover;
        for (int i = 0; i < Constants.AREA_NUM; i++) {
            ap_cover = new APCover(i, ap_cover_set[i].size());
            aps_descender_area.add(ap_cover);
        }

        APBSselection apbs_selection = new APBSselection(_scenario);

        int reconnect_ap;
        while (!aps_descender_area.isEmpty()) {
            ap_cover = aps_descender_area.poll();
            for (int i = 0; i < area_wifi_users[ap_cover.getAreaId()].size(); i++) {
                reconnect_ap = apbs_selection.SelectAP(area_wifi_users[ap_cover.getAreaId()].get(i));
                area_wifi_users[ap_cover.getAreaId()].get(i).ChangeConnectedAP(_wifi_ap[reconnect_ap]);
                _wifi_ap[reconnect_ap].UserList.add(area_wifi_users[ap_cover.getAreaId()].get(i));
                _wifi_ap[reconnect_ap].connecting_num++;
                _wifi_ap[reconnect_ap].reconnect(event_time, _param.service_set);
            }

            //※すでに接続済みのやつはsize=0
            for (int i = 0; i < area_lteu_users[ap_cover.getAreaId()].size(); i++) {//※※ここでのLTE-UユーザはLTE-Uに接続できないやつだけ
                reconnect_ap = apbs_selection.SelectAPorBS(area_lteu_users[ap_cover.getAreaId()].get(i));
                if (reconnect_ap < 10000) {
                    area_lteu_users[ap_cover.getAreaId()].get(i).ChangeConnectedAP(_wifi_ap[reconnect_ap]);
                    _wifi_ap[reconnect_ap].UserList.add(area_lteu_users[ap_cover.getAreaId()].get(i));
                    _wifi_ap[reconnect_ap].connecting_num++;
                } else {
                    System.out.println("RECONNECT_ERROR");
                    area_lteu_users[ap_cover.getAreaId()].get(i).ChangeConnectedAP(_lteu_bs[reconnect_ap]);
                    _lteu_bs[reconnect_ap - 10000].UserList.add(area_lteu_users[ap_cover.getAreaId()].get(i));
                    _lteu_bs[reconnect_ap - 10000].connecting_num++;
                    _lteu_bs[reconnect_ap - 10000].reconnect(event_time, _param.service_set);
                }
            }
        }

        //3.LTE-U→WiFiへのオフロード(LTE-UのID順に見て、オフロードユーザを選択)(※候補が少ないところから選んだほうが良いかも)
        int offload_ap;
        for (int i = 0; i < Constants.LTEU_NUM; i++) {//※※ユーザから得られる重複APリストとエリアから得られるリストが正しいか確認
            for (int j = 0; j < _lteu_bs[i].UserList.size(); j++) {
                offload_ap = apbs_selection.SelectAPorBS(_lteu_bs[i].UserList.get(j));
                if (offload_ap < 10000) {//LTE-U BS同士は重複していないことを前提にしている
                    _lteu_bs[i].UserList.get(j).ChangeConnectedAP(_wifi_ap[offload_ap]);
                    _wifi_ap[offload_ap].UserList.add(_lteu_bs[i].UserList.get(j));
                    _wifi_ap[offload_ap].connecting_num++;
                    _wifi_ap[offload_ap].reconnect(event_time, _param.service_set);

                    _lteu_bs[i].UserList.remove(j);
                    _lteu_bs[i].connecting_num--;
                    _lteu_bs[i].reconnect(event_time, _param.service_set);
                }
            }//System.out.println(i+"----"+_lteu_bs[i].UserList.size()+"\t" +  _lteu_bs[i].connecting_num);
        }
    }

    /*個体の評価をする*/
    private double Evaluate(int[][] individual) {

        double[] temp_evaluation = new double[Constants.LTEU_NUM + Constants.WiFi_NUM];
        double temp_min_evaluataion = 999999;

        for (int i = 0; i < Constants.WiFi_NUM; i++) {

            if (_wifi_ap[i].ap_id == individual[i][0]) {
                _wifi_ap[i].assigned_channel = individual[i][1];
            } else {
                System.out.println("WiFi_ERROR");
            }
        }

        //2. 各AP, BSの容量をセット
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].interference_list = ci.LTEUCheck(i, _wifi_ap, _lteu_bs);
            _lteu_bs[i].SetCapacity();
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].interference_list = ci.WiFiCheck(i, _wifi_ap, _lteu_bs);
            _wifi_ap[i].SetCapacity();
        }

        /* ユーザの接続先選択 */
        ProposedReConnect(individual);

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            temp_evaluation[i] = Utility.CalcUserthroughput(_lteu_bs[i].capacity, _lteu_bs[i].connecting_num);

        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            temp_evaluation[i + Constants.LTEU_NUM] = Utility.CalcUserthroughput(_wifi_ap[i].capacity, _wifi_ap[i].connecting_num);
        }

        //3. 評価値を決定:最小のスループット 
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            if (temp_min_evaluataion > temp_evaluation[i] && temp_evaluation[i] > 0) {
                temp_min_evaluataion = temp_evaluation[i];

            }

        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            if (temp_min_evaluataion > temp_evaluation[i + Constants.LTEU_NUM] && temp_evaluation[i + Constants.LTEU_NUM] > 0) {
                temp_min_evaluataion = temp_evaluation[i + Constants.LTEU_NUM];
            }

        }
//        if(temp_min_evaluataion == 3.5){
//        System.out.println(temp_min_evaluataion);
//        }
        return temp_min_evaluataion;
    }

    /*個体の評価をする:総容量の最大回*/
    private double EvaluateSumCapacity(int[][] individual) {

//        double[] temp_evaluation = new double[Constants.LTEU_NUM + Constants.WiFi_NUM];
        double total_throughput = 0;
//        double total_connecting_num = 0;

        //1.個体のチャネル割り当て情報を記録
//        for (int i = 0; i < Constants.LTEU_NUM; i++) {
//
//            if (_lteu_bs[i].ap_id == individual[i][0]) {
//                _lteu_bs[i].assigned_channel = individual[i][1];
//            } else {
//                System.out.println("LTEU_ERROR");
//            }
//        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {

            if (_wifi_ap[i].ap_id == individual[i][0]) {
                _wifi_ap[i].assigned_channel = individual[i][1];
            } else {
                System.out.println("WiFi_ERROR");
            }
        }

        //2. 各AP, BSの容量をセット
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].interference_list = ci.LTEUCheck(i, _wifi_ap, _lteu_bs);
            _lteu_bs[i].SetCapacity();
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].interference_list = ci.WiFiCheck(i, _wifi_ap, _lteu_bs);
            _wifi_ap[i].SetCapacity();
        }

        /* ユーザの接続先選択 */
//        ProposedReConnect(individual);
//          ReConnectNotChange(individual);
        for (int i = 0; i < Constants.LTEU_NUM; i++) {

            total_throughput += _lteu_bs[i].capacity;

        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {

            total_throughput += _wifi_ap[i].capacity;

        }

        //3. 評価値を決定:平均のスループット 
//        average_throughput = average_throughput / total_connecting_num;
//        if(temp_min_evaluataion == 3.5){
//        System.out.println(temp_min_evaluataion);
//        }
        return total_throughput;
    }

}
