/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;

import com.lilium.snake.laaandwifisimulator.network.ActionChannel;

/**
 * 提案手法 : GAによるチャネル割り当て
 *
 * @author ginnan
 */
public class EventOfDQN extends Event {

    private final WiFiAP _wifi_ap[];
    private final LTEUBS _lteu_bs[];
    private final int[] _actionChannel;
    CheckInterference ci;
    Random rn;

    int wifi_num; // LTE+WiFiユーザのうち、WiFiにつないでいたユーザ数
    int lte_num; // LTE+WiFiユーザのうち、LTE-Uにつないでいたユーザ数

    public EventOfDQN(double time, Scenario scenario, int[] actionChannel) {
        super(scenario);
        this.event_time = time;
        _wifi_ap = _area.getWiFiAP();
        _lteu_bs = _area.getLTEUBS();
        _actionChannel = actionChannel;

        ci = new CheckInterference(_area);
        rn = new Random();

        wifi_num = 0;
        lte_num = 0;
    }

    @Override
    public void runEvent() throws IOException {
        StartAlgorithm();

    }

    private void StartAlgorithm() throws IOException {

        // Up to this point, the process for collecting the statistics so far
        FirstReConnect();
        int[][] individual = CreateFirstIndividuals();
        // 最大の評価値の個体をBS,APに適用し、接続先を変更

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].assigned_channel = individual[i][1];
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].assigned_channel = individual[i + Constants.LTEU_NUM][1];
        }

        // 干渉の容量をセット
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].interference_list = ci.LTEUCheck(i, _wifi_ap, _lteu_bs);
            _lteu_bs[i].SetCapacity();
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].interference_list = ci.WiFiCheck(i, _wifi_ap, _lteu_bs);
            _wifi_ap[i].SetCapacity();

        }

        // ここで、ユーザの接続先選択をする
        // Here, select the user's connection destination
        ProposedReConnect(individual);
        // ReConnectNotChange(individual);

        _area.CopyLTEUBS(_lteu_bs);
        _area.CopyWiFiAP(_wifi_ap);

        _scenario.gettimeMinData(event_time);
        // _queue.cleanEventQueue();
        // Event sim_event = new EventOfSimEnd(_scenario);
        // _queue.add(sim_event);

    }

    /* Generate individuals in the initial population: Random allocation */
    private int[][] CreateFirstIndividuals() {

        int[][] temp_individual = new int[Constants.LTEU_NUM + Constants.WiFi_NUM][2];
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            temp_individual[i][0] = _lteu_bs[i].ap_id;
            temp_individual[i][1] = _lteu_bs[i].assigned_channel;// LTE-U does not change channel
                                                                 // //rn.nextInt (Constants.CHANNEL_NUM +
                                                                 // 1) --1; // If -1, do not assign a channel
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            temp_individual[i + Constants.LTEU_NUM][0] = _wifi_ap[i].ap_id;
            temp_individual[i + Constants.LTEU_NUM][1] = _wifi_ap[i].assigned_channel; // -1の場合、チャネルを割り当てない
        }
        if (_actionChannel.isWifi) {
            temp_individual[_actionChannel.id + Constants.LTEU_NUM][0] = _actionChannel.id;
            temp_individual[_actionChannel.id + Constants.LTEU_NUM][1] = _actionChannel.channel;
        } else {
            temp_individual[_actionChannel.id - 10000][0] = _actionChannel.id;
            temp_individual[_actionChannel.id - 10000][1] = _actionChannel.channel;
        }

        // 制約条件を満たしているかを確認
        return temp_individual;

    }

    /* 提案手法のユーザの接続先変更 */
    private void ProposedReConnect(int[][] individual) {

        // エリアごとのユーザ数を保持
        int[] area_wifi_user_num = new int[Constants.AREA_NUM];
        int[] area_lteu_user_num = new int[Constants.AREA_NUM];

        // エリアごとのUserNodeを保持
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

        // AP,BSのユーザ情報のクリア ＆個体のチャネル割り当てに更新
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].assigned_channel = individual[i + Constants.LTEU_NUM][1];
            _wifi_ap[i].UserList.clear();
            _wifi_ap[i].connecting_num = 0;
            _wifi_ap[i].user_throughput = 0;
        }

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].assigned_channel = individual[i][1];
            _lteu_bs[i].UserList.clear();
            _lteu_bs[i].connecting_num = 0;
            _lteu_bs[i].user_throughput = 0;
        }

        // 各エリアでどのAP,BSがそのエリアをカバーしているかのリスト
        ArrayList<Integer>[] ap_cover_set;
        ArrayList<Integer>[] bs_cover_set;

        ap_cover_set = _area.getAreaCooveredAPSet();
        bs_cover_set = _area.getAreaCooveredBSSet();

        // 1. WiFi + LTEユーザを接続可能ならLTE-U BSに接続(BS同士はセルが重複していないと仮定)
        // for (int i = 0; i < Constants.AREA_NUM; i++)
        // {//*********************************
        // if (bs_cover_set[i].size() != 0) {
        // for (int k = 0; k < bs_cover_set[i].size(); k++) {
        // //System.out.println(area_lteu_user_num[i]+"**--*--"+
        // area_lteu_users[i].size());
        // while (!area_lteu_users[i].isEmpty()) {
        // _lteu_bs[bs_cover_set[i].get(k)].UserList.add(area_lteu_users[i].poll());
        // } // System.out.println(_lteu_bs[bs_cover_set[i].get(k)].UserList.size()+"\t"
        // +area_lteu_users[i].size());
        // Utility.ChangeConnectingAPs(_lteu_bs[bs_cover_set[i].get(k)]);
        // _lteu_bs[bs_cover_set[i].get(k)].connecting_num += area_lteu_user_num[i];
        // _lteu_bs[bs_cover_set[i].get(k)].reconnect(event_time, _param.service_set);
        // }
        // }
        // }//*****************************
        // BSが複数ある場合の処理****************************************************************
        APBSselection apbs_selection_try = new APBSselection(_scenario);
        int bs = -1;

        int bs_num = 1; // bs_numを--したら、 Constants.LTEU_AREA_COVER_NUMも--にならないよね
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
        // 終了*********************************************************

        PriorityQueue<APCover> aps_descender_area = new PriorityQueue<>(new MyComparator());
        // 2.WiFiのみユーザ, LTE + WiFiユーザの接続先WiFi APを決定
        // 接続先候補の少ないユーザ順に並べる⇒カバーAP数の少ないエリア順に並べることと等価
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

            // ※すでに接続済みのやつはsize=0
            for (int i = 0; i < area_lteu_users[ap_cover.getAreaId()].size(); i++) {// ※※ここでのLTE-UユーザはLTE-Uに接続できないやつだけ
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

        // 3.LTE-U→WiFiへのオフロード(LTE-UのID順に見て、オフロードユーザを選択)(※候補が少ないところから選んだほうが良いかも)
        int offload_ap;
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            for (int j = 0; j < _lteu_bs[i].UserList.size(); j++) {
                offload_ap = apbs_selection.SelectAPorBSforAlgorithm(_lteu_bs[i].UserList.get(j));
                if (offload_ap < 10000) {// LTE-U BS同士は重複していないことを前提にしている
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
    private void FirstReConnect() throws IOException {

        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            _lteu_bs[i].reconnect(event_time, _param.service_set);
            lte_num += _lteu_bs[i].connecting_num;

        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _wifi_ap[i].reconnect(event_time, _param.service_set);

            for (int x = 0; x < _wifi_ap[i].UserList.size(); x++) {
                if (_wifi_ap[i].UserList.get(x).user_set == 1) {
                    wifi_num++;
                }

            }
        }

    }

    // カバーしているAPが少ないエリア順に
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

    // 干渉が多い順に並べる
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

    public void writeToFile_wifi_lte_num() throws IOException {
        FileWriter fw = new FileWriter("./WiFiLTEU_Data/NumNum/" + Constants.are + "num.dat", true);
        BufferedWriter bw = new BufferedWriter(fw);

        String text = wifi_num + "\t" + lte_num + "\n";

        bw.write(text);

        bw.close();
    }
}
