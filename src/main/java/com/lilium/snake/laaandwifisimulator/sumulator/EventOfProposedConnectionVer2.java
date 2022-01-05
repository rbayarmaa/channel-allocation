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
 * 提案手法の接続先変更だけやる場合のやつ(Ver.2作ろうと思ったが、作っていない)
 *
 * @author ginnan
 */
public class EventOfProposedConnectionVer2 extends Event {

    private final WiFiAP _wifi_ap[];
    private final LTEUBS _lteu_bs[];

    public EventOfProposedConnectionVer2(double time, Scenario scenario) {
        super(scenario);

        this.event_time = time;
        _wifi_ap = _area.getWiFiAP();
        _lteu_bs = _area.getLTEUBS();
    }

    @Override
    public void runEvent() throws IOException {
        StartConnection();
    }

    private void StartConnection() throws IOException {


        //デバッグ*****************
//        double th_p = 999999;
//        for (int i = 0; i < Constants.LTEU_NUM; i++) {
////            if(_wifi_ap[i].assigned_channel == -1) System.out.println(i + "\t"+_wifi_ap[i].located_area_id + "\t" + _wifi_ap[i].assigned_channel + "\t" + _wifi_ap[i].interference_list.size() + "\t" +_wifi_ap[i].UserList.size());
////            if(_lteu_bs[i].connecting_num == 0) 
////            System.out.println(i + "\t" + _lteu_bs[i].connecting_num + "\t" + _lteu_bs[i].capacity + "\t" + _lteu_bs[i].user_throughput);
//            if (_lteu_bs[i].user_throughput != 0 && th_p > _lteu_bs[i].user_throughput) {
//                th_p = _lteu_bs[i].user_throughput;
//            }
//        }
//        System.out.println(th_p + "***********************preevious");
//        th_p = 999999;
//        for (int i = 0; i < Constants.WiFi_NUM; i++) {
////            System.out.println(i + "\t" + _wifi_ap[i].connecting_num + "\t" + _wifi_ap[i].capacity + "\t" + _wifi_ap[i].user_throughput+ "\t" + _wifi_ap[i].located_area_id);
//            if (_wifi_ap[i].user_throughput != 0 && th_p > _wifi_ap[i].user_throughput) {
//                th_p = _wifi_ap[i].user_throughput;
//            }
//        }
//        System.out.println(th_p + "-------------------------previous");
        //***********************


        FirstReConnect();

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

        //デバッグ****************************
//        int sum_w =0;
//        int sum_l=0;
//        for(int z=0; z < area_wifi_user_num.length;z++){
//            sum_w += area_wifi_user_num[z];
//            sum_l += area_lteu_user_num[z];
//        }
//        System.out.println(sum_w);
//        System.out.println(sum_l);
//        System.out.println();
        //*******************************************

        //デバッグ-------------------------------------------
//         System.out.println("@@@@@@@@@@@@@@@@@@");
//        for(int z=0; z < area_wifi_user_num.length;z++){
//            System.out.println(z+"\t"+area_wifi_user_num[z] + "\t" +area_lteu_user_num[z]);
//        }
//        System.out.println();
        //---------------------------------------------------

        //AP,BSのユーザ情報のクリア ＆個体のチャネル割り当てに更新
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].UserList.clear();
            _wifi_ap[i].connecting_num = 0;
            _wifi_ap[i].user_throughput = 0;
        }

        for (int i = 0; i < Constants.LTEU_NUM; i++) {//System.out.println(i+"***"+_lteu_bs[i].UserList.size()+"\t" +  _lteu_bs[i].connecting_num);
            _lteu_bs[i].UserList.clear();
            _lteu_bs[i].connecting_num = 0;
            _lteu_bs[i].user_throughput = 0;
        }

        //各エリアでどのAP,BSがそのエリアをカバーしているかのリスト
        ArrayList<Integer>[] ap_cover_set;
        ArrayList<Integer>[] bs_cover_set;

        ap_cover_set = _area.getAreaCooveredAPSet();
        bs_cover_set = _area.getAreaCooveredBSSet();

        LinkedList<Integer> multiple_bs_area = new LinkedList<>(); //2つのBSでカバーされているエリアの集合

        //1. WiFi + LTEユーザを接続可能ならLTE-U BSに接続(BS同士はセルが重複していないと仮定)⇒【LTE-U BSが重複しててもいいように後で直す】
//        for (int i = 0; i < Constants.AREA_NUM; i++) {
//            if (bs_cover_set[i].size() != 0) {
//                for (int k = 0; k < bs_cover_set[i].size(); k++) {      //System.out.println(area_lteu_user_num[i]+"**--*--"+ area_lteu_users[i].size());
//                    while (!area_lteu_users[i].isEmpty()) {
//                        _lteu_bs[bs_cover_set[i].get(k)].UserList.add(area_lteu_users[i].poll());
//                    }                             // System.out.println(_lteu_bs[bs_cover_set[i].get(k)].UserList.size()+"\t" +area_lteu_users[i].size());
//                    Utility.ChangeConnectingAPs(_lteu_bs[bs_cover_set[i].get(k)]);
//                    _lteu_bs[bs_cover_set[i].get(k)].connecting_num += area_lteu_user_num[i];
//                    _lteu_bs[bs_cover_set[i].get(k)].reconnect(event_time, _param.service_set);
//
//                }
//            }
//        }
        //試し書き(BSが複数ある場合の処理)****************************************************************
        APBSselection apbs_selection_try = new APBSselection(_scenario);
        int bs = -1;

        int bs_num = 1; //bs_numを--したら、 Constants.LTEU_AREA_COVER_NUMも--にならないよね
        while (bs_num != Constants.LTEU_AREA_COVER_NUM + 1) {
            for (int i = 0; i < Constants.AREA_NUM; i++) {
                if (bs_cover_set[i].size() == bs_num) {
                    while (!area_lteu_users[i].isEmpty()) {
                        bs = apbs_selection_try.SelectBS(area_lteu_users[i].peek());
                        area_lteu_users[i].peek().ChangeConnectedAP(_lteu_bs[bs - 10000]);
                        _lteu_bs[bs - 10000].UserList.add(area_lteu_users[i].poll());
                        _lteu_bs[bs - 10000].connecting_num++;
                        _lteu_bs[bs - 10000].reconnect(event_time, _param.service_set);
                    }

                }
            }
            bs_num++;
        }
        //試し書き終了*********************************************************

//        int multiple_bs_area_id = -1;
//        //20171207 追加 2つのBSでカバーされているエリアの処理
//        while(!multiple_bs_area.isEmpty()){
//            for(int j = 0; j < multiple_bs_area.size(); j++){
//                multiple_bs_area_id = multiple_bs_area.get(j);
//            }
//            
//        }
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
                    _wifi_ap[reconnect_ap].reconnect(event_time, _param.service_set);//いるよね??
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
//        for(int z=0; z < 3;z++){//試し
        for (int i = 0; i < Constants.LTEU_NUM; i++) {//※※ユーザから得られる重複APリストとエリアから得られるリストが正しいか確認
            for (int j = 0; j < _lteu_bs[i].UserList.size(); j++) {//System.out.println(_lteu_bs[i].UserList.size());
                offload_ap = apbs_selection.SelectAPorBSforAlgorithm(_lteu_bs[i].UserList.get(j));  //System.out.println(offload_ap + "\t" + _lteu_bs[i].ap_id);
                if (offload_ap < 10000) {//LTE-U BS同士は重複していないことを前提にしている
                    _lteu_bs[i].UserList.get(j).ChangeConnectedAP(_wifi_ap[offload_ap]);
                    _wifi_ap[offload_ap].UserList.add(_lteu_bs[i].UserList.get(j));
                    _wifi_ap[offload_ap].connecting_num++;
                    _wifi_ap[offload_ap].reconnect(event_time, _param.service_set);

                    _lteu_bs[i].UserList.remove(j);
                    _lteu_bs[i].connecting_num--;
                    _lteu_bs[i].reconnect(event_time, _param.service_set);
                    j--;
                }
            }//System.out.println(i+"----"+_lteu_bs[i].UserList.size()+"\t" +  _lteu_bs[i].connecting_num);
        }
//    }//試し

        //デバッグ*****************
//        double th = 999999;
//        for (int i = 0; i < Constants.LTEU_NUM; i++) {
////            if(_wifi_ap[i].assigned_channel == -1) System.out.println(i + "\t"+_wifi_ap[i].located_area_id + "\t" + _wifi_ap[i].assigned_channel + "\t" + _wifi_ap[i].interference_list.size() + "\t" +_wifi_ap[i].UserList.size());
////            if(_lteu_bs[i].connecting_num == 0) 
//            System.out.println(i + "\t" + _lteu_bs[i].connecting_num + "\t" + _lteu_bs[i].capacity + "\t" + _lteu_bs[i].user_throughput);
//            if (_lteu_bs[i].user_throughput != 0 && th > _lteu_bs[i].user_throughput) {
//                th = _lteu_bs[i].user_throughput;
//            }
//        }
//        System.out.println(th + "***********************");
//        th = 999999;
//        for (int i = 0; i < Constants.WiFi_NUM; i++) {
//            System.out.println(i + "\t" + _wifi_ap[i].connecting_num + "\t" + _wifi_ap[i].capacity + "\t" + _wifi_ap[i].user_throughput+ "\t" + _wifi_ap[i].located_area_id);
//            if (_wifi_ap[i].user_throughput != 0 && th > _wifi_ap[i].user_throughput) {
//                th = _wifi_ap[i].user_throughput;
//            }
//        }
//        System.out.println(th + "-------------------------");

//        if(th_p > th){System.out.println("×" + th_p + "\t"+ th);}
        //***********************

        /*最小スループットの時間平均の処理*/
        _scenario.gettimeMinData(event_time);

        //***********************************************
//        for(int i = 0; i < area_wifi_users.length; i++){
//            area_wifi_users[i].clear();
//            area_lteu_users[i].clear();
//        }
//        
        //*************************************************

        /*次の提案手法の発生イベントを作る*/
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfProposedConnectionVer2(time, _scenario);
        _queue.add(next_event);

    }

    /* 初期個体の評価で誤った最小スループットが入らないようにする */
    private void FirstReConnect() {

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].reconnect(event_time, _param.service_set);
            _lteu_bs[i].user_throughput = 0;
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].reconnect(event_time, _param.service_set);
            _wifi_ap[i].user_throughput = 0;
        }

    }
}
