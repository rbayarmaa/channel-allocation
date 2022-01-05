/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * 比較手法:ユーザが多いエリアから順にチャネル割り当て
 *
 * @author ginnan
 */
public class EventOfDynamicAssign extends Event {

    private final WiFiAP _wifi_ap[];
    private final LTEUBS _lteu_bs[];

    CheckInterference ci;

    public EventOfDynamicAssign(double time, Scenario scenario) {
        super(scenario);

        this.event_time = time;
        _wifi_ap = _area.getWiFiAP();
        _lteu_bs = _area.getLTEUBS();

        ci = new CheckInterference(_area);
    }

    @Override
    public void runEvent() throws IOException {
        StartAssign();
    }

    private void StartAssign() throws IOException {

        FirstReConnect();

        ProposedReConnect();

        /*最小スループットの時間平均の処理*/
        _scenario.gettimeMinData(event_time);

        /*次の提案手法の発生イベントを作る*/
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfDynamicAssign(time, _scenario);
        _queue.add(next_event);

    }

    /* 提案手法のユーザの接続先変更 + 動的なチャネル割り当て */
    private void ProposedReConnect() {

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
            }
        }

        //AP,BSのユーザ情報のクリア ＆個体のチャネル割り当てに更新
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].UserList.clear();
            _wifi_ap[i].connecting_num = 0;
            _wifi_ap[i].user_throughput = 0;
        }

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].UserList.clear();
            _lteu_bs[i].connecting_num = 0;
            _lteu_bs[i].user_throughput = 0;
        }

        //各エリアでどのAP,BSがそのエリアをカバーしているかのリスト
        ArrayList<Integer>[] ap_cover_set;
        ArrayList<Integer>[] bs_cover_set;

        ap_cover_set = _area.getAreaCooveredAPSet();
        bs_cover_set = _area.getAreaCooveredBSSet();

        //***動的なチャネル割り当てを行う ****//
        PriorityQueue<APCover> user_descender_ap = new PriorityQueue<>(new MyComparator_v2());
        //1.まず、チャネルを割当てない状態に,かつ、WiFiのカバーエリアにいるユーザ順(すべてのユーザ)にAPをソート
        int area_user_num;
        APCover user_area_sort;
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].assigned_channel = -1;
            area_user_num = 0;
            for (int j = 0; j < _wifi_ap[i].cover_area_list.size(); j++) {
                if (_wifi_ap[i].cover_area_list.get(j) != -1) {
                    area_user_num += (area_wifi_user_num[_wifi_ap[i].cover_area_list.get(j)] + area_lteu_user_num[_wifi_ap[i].cover_area_list.get(j)]);

                }
            }

            user_area_sort = new APCover(i, area_user_num);// ※area_id=ap_id, cover_ap_num= area_user_numとして用いている
            user_descender_ap.add(user_area_sort);

        }

        //2.カバーエリアにいるユーザ数が多い順に干渉なくチャネルを割り当て(ユーザがいないAPには割り当てない)
        APCover user_ap;
        while (!user_descender_ap.isEmpty()) {
            user_ap = user_descender_ap.poll();
            if (user_ap.getCoverAPNum() != 0) {
                _wifi_ap[user_ap.getAreaId()].assigned_channel = ci.WiFiLeastInterference(user_ap.getAreaId(), _wifi_ap, _lteu_bs);
            }
        }

        //3.制約条件を確認
        boolean flag = true;
        int covered_channel;
        ArrayList<Integer>[] ap_cover_area = _area.getAreaCooveredAPSet();

        ArrayList<Integer> non_ap_area_list = new ArrayList<>();
        for (int a = 0; a < Constants.AREA_NUM; a++) {
            covered_channel = -1;
            for (int b = 0; b < ap_cover_area[a].size(); b++) {
                if (_wifi_ap[ap_cover_area[a].get(b)].assigned_channel != -1) {
                    covered_channel = _wifi_ap[ap_cover_area[a].get(b)].assigned_channel;
                }
            }

            if (covered_channel == -1) {//制約条件を満たさない
                non_ap_area_list.add(a);
            }
        }

        //4.カバーされていないエリアをカバーするようにAPにチャネル割り当て
        if (non_ap_area_list.size() != 0) {
            APCover cover_area_order_ap;
            ArrayList<APCover> cover_area_order = new ArrayList<>();
            int max_cover_ap = 0;
            int cover_ap = 0;
            int assign_ap = -1;
            //カバーされていないエリアをカバーしているAPをエリアが多い順にソート
            for (int i = 0; i < Constants.WiFi_NUM; i++) {
                cover_ap = 0;
                for (int j = 0; j < _wifi_ap[i].cover_area_list.size(); j++) {
                    for (int k = 0; k < non_ap_area_list.size(); k++) {
                        if (Objects.equals(_wifi_ap[i].cover_area_list.get(j), non_ap_area_list.get(k))) {
                            cover_ap++;
                        }
                    }
                }

                if (cover_ap != 0) {
                    cover_area_order_ap = new APCover(i, cover_ap);
                    cover_area_order.add(cover_area_order_ap);
                }
            }

            //カバーエリアが多いAPから順に割り当てていく
//        cover_area_order_ap = cover_area_order.poll();
            while (!non_ap_area_list.isEmpty()) {
                max_cover_ap = 0;
                for (int x = 0; x < cover_area_order.size(); x++) {
                    cover_ap = cover_area_order.get(x).getCoverAPNum();
                    if (max_cover_ap <= cover_ap) {
                        if (_wifi_ap[cover_area_order.get(x).getAreaId()].assigned_channel == -1) {
                            max_cover_ap = cover_ap;
                            assign_ap = cover_area_order.get(x).getAreaId();
                        }
                    }
                }

                _wifi_ap[assign_ap].assigned_channel = ci.WiFiLeastInterference(assign_ap, _wifi_ap, _lteu_bs);

                for (int y = 0; y < _wifi_ap[assign_ap].cover_area_list.size(); y++) {
                    for (int z = 0; z < non_ap_area_list.size(); z++) {
                        if (Objects.equals(non_ap_area_list.get(z), _wifi_ap[assign_ap].cover_area_list.get(y))) {
                            non_ap_area_list.remove(z);
                            break;
                        }
                    }
                }
            }
        }

        //5.干渉時の容量をセット
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].interference_list = ci.LTEUCheck(i, _wifi_ap, _lteu_bs);
            _lteu_bs[i].SetCapacity();
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].interference_list = ci.WiFiCheck(i, _wifi_ap, _lteu_bs);
            _wifi_ap[i].SetCapacity();

        }

        //1. WiFi + LTEユーザを接続可能ならLTE-U BSに接続(BS同士はセルが重複していないと仮定)
//        for (int i = 0; i < Constants.AREA_NUM; i++) {//*********************************
//            if (bs_cover_set[i].size() != 0) {
//                for (int k = 0; k < bs_cover_set[i].size(); k++) {      //System.out.println(area_lteu_user_num[i]+"**--*--"+ area_lteu_users[i].size());
//                    while (!area_lteu_users[i].isEmpty()) {
//                        _lteu_bs[bs_cover_set[i].get(k)].UserList.add(area_lteu_users[i].poll());
//                    }                             // System.out.println(_lteu_bs[bs_cover_set[i].get(k)].UserList.size()+"\t" +area_lteu_users[i].size());
//                    Utility.ChangeConnectingAPs(_lteu_bs[bs_cover_set[i].get(k)]);
//                    _lteu_bs[bs_cover_set[i].get(k)].connecting_num += area_lteu_user_num[i];
//                    _lteu_bs[bs_cover_set[i].get(k)].reconnect(event_time, _param.service_set);
//                }
//            }
//        }//*****************************
        //1. WiFi + LTEユーザを接続可能ならLTE-U BSに接続(BSが複数ある場合の処理)****************************************************************
        APBSselection apbs_selection_try = new APBSselection(_scenario);
        int bs = -1;

        int bs_num = 1;
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
        //*********************************************************

        PriorityQueue<APCover> aps_descender_area = new PriorityQueue<>(new MyComparator());
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
            for (int i = 0; i < area_lteu_users[ap_cover.getAreaId()].size(); i++) {//
                reconnect_ap = apbs_selection.SelectAPorBS(area_lteu_users[ap_cover.getAreaId()].get(i));
                if (reconnect_ap < 10000) {
                    area_lteu_users[ap_cover.getAreaId()].get(i).ChangeConnectedAP(_wifi_ap[reconnect_ap]);
                    _wifi_ap[reconnect_ap].UserList.add(area_lteu_users[ap_cover.getAreaId()].get(i));
                    _wifi_ap[reconnect_ap].connecting_num++;
                    _wifi_ap[reconnect_ap].reconnect(event_time, _param.service_set);
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
        for (int i = 0; i < Constants.LTEU_NUM; i++) {//
            for (int j = 0; j < _lteu_bs[i].UserList.size(); j++) {
                offload_ap = apbs_selection.SelectAPorBSforAlgorithm(_lteu_bs[i].UserList.get(j));
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
            }
        }
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

    //カバーしているAPが少ないエリア順に
    static class MyComparator implements Comparator<APCover> {

        @Override
        public int compare(APCover arg0, APCover arg1) {
            APCover x = arg0;
            APCover y = arg1;

            if (x.getCoverAPNum() > y.getCoverAPNum()) {
                return 1;
            } else if (x.getCoverAPNum() < y.getCoverAPNum()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    //カバーしているAP(カバーエリアにいるユーザ数)が多いエリア順に
    static class MyComparator_v2 implements Comparator<APCover> {

        @Override
        public int compare(APCover arg0, APCover arg1) {
            APCover x = arg0;
            APCover y = arg1;

            if (x.getCoverAPNum() < y.getCoverAPNum()) {
                return 1;
            } else if (x.getCoverAPNum() > y.getCoverAPNum()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    //干渉が多い順に並べる
    static class MyComparator2 implements Comparator<InterferenceOrder> {

        @Override
        public int compare(InterferenceOrder arg0, InterferenceOrder arg1) {
            InterferenceOrder x = arg0;
            InterferenceOrder y = arg1;

            if (x.inteference_num < y.inteference_num) {
                return 1;
            } else if (x.inteference_num > y.inteference_num) {
                return -1;
            } else {
                return 0;
            }
        }
    }

}
