/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * 評価環境
 *
 * @author ginnan
 */
public class Area {

    //    LinkedList<Integer>[] deployment_id;
    /*WiFi APの配置エリアに関する情報*/
    private int ap_bs_position_num = Constants.AP_BS_POSITION_NUM;     //AP,BSが置ける位置の数
    private int ap_cover_num = Constants.AP_COVER_NUM;   //APがカバーしているエリアの数
    private int[][] ap_position_cover_area = new int[ap_bs_position_num][ap_cover_num];    // WiFi APがカバーしているエリアの情報
    private int area_covered_ap_num = Constants.AREA_COVERED_AP_NUM;    //最小エリアをカバーしているAPの数(APがすべての位置に配置された場合を仮定)
    private int[][] area_ap_cover = new int[Constants.AREA_NUM][area_covered_ap_num];  //最小エリアをカバーしているAPの情報

    /*LTE-U BSの配置エリアに関する情報*/
    private int bs_cover_num = Constants.BS_COVER_NUM;   //最小エリアをカバーしているBSの数
    private int[][] bs_position_cover_area = new int[ap_bs_position_num][bs_cover_num];    // LTE-U BSがカバーしているエリアの情報
    private int area_covered_bs_num = Constants.AREA_COVERED_BS_NUM;    //最小エリアをカバーしているBSの数(BSがすべての位置に配置された場合を仮定)
    private int[][] area_bs_cover = new int[Constants.AREA_NUM][area_covered_bs_num];  //最小エリアをカバーしているBSの情報
    private int[] lteu_place = new int[Constants.LTEU_NUM];
    private int[] lteu_channel_assign = new int[Constants.LTEU_NUM];

    private WiFiAP wifi_ap[] = new WiFiAP[Constants.WiFi_NUM];
    private LTEUBS lteu_bs[] = new LTEUBS[Constants.LTEU_NUM];

    private int deploy_check[];     //APが置ける位置にAPが配置されているかどうか 1:配置されている, 0:配置されていない
    private int lte_deploy_check[];     //BSが置ける位置にAPが配置されているかどうか 1:配置されている, 0:配置されていない
    private ArrayList<Integer> area_coverd_set[] = new ArrayList[Constants.AREA_NUM];   //最小エリアがどのAPにカバーされているかをセット
    private ArrayList<Integer> bs_area_coverd_set[] = new ArrayList[Constants.AREA_NUM];   //最小エリアがどのBSにカバーされているかをセット

    private final Scenario _scenario;
//    private final UserParameter _param;

    public Area(Scenario scenario, AreaTopology topology) {
        _scenario = scenario;
        Random rn = new Random();
//        _param = _scenario.getUserParameter() ;

        //csvから取得
        getTopology(topology);

        for (int i = 0; i < Constants.AREA_NUM; i++) {
            area_coverd_set[i] = new ArrayList<>();
            bs_area_coverd_set[i] = new ArrayList<>();
        }

        int located_area;

        /*LTE-U BSの作成  : 配置場所固定⇒配置場所をcsvから読み取る*/
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            lteu_bs[i] = new LTEUBS(i + 10000);
            lteu_bs[i].assigned_channel = lteu_channel_assign[i];
        }

        lte_deploy_check = new int[Constants.AP_BS_POSITION_NUM];

        for (int j = 0; j < Constants.AP_BS_POSITION_NUM; j++) {
            lte_deploy_check[j] = 0;
        }

        //LTEの配置、カバーしているエリアをセット
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            lteu_bs[i].located_area_id = lteu_place[i];
            lte_deploy_check[lteu_bs[i].located_area_id] = 1;

            for (int a = 0; a < bs_cover_num; a++) {
                if (bs_position_cover_area[lteu_bs[i].located_area_id][a] != -1) {
                    lteu_bs[i].cover_area_list.add(bs_position_cover_area[lteu_bs[i].located_area_id][a]); //BSがカバーしているエリアをセット
                    bs_area_coverd_set[bs_position_cover_area[lteu_bs[i].located_area_id][a]].add(i); //最小エリアをカバーしているBSをセット
                }
            }
        }
        /*LTE-U BSの作成完了 */

        /*WiFi APの作成 */
        deploy_check = new int[ap_bs_position_num];

        //WiFi APを作成＆チャネル割り当て
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            wifi_ap[i] = new WiFiAP(i);
            wifi_ap[i].assigned_channel = rn.nextInt(Constants.CHANNEL_NUM);
        }

        int wifi_ap_set = 0;

        while (wifi_ap_set == 0) {//全てのエリアがWiFiでカバーされるまで再配置する
            wifi_ap_set = 1;
            for (int j = 0; j < ap_bs_position_num; j++) {
                deploy_check[j] = 0;
            }

            for (int i = 0; i < Constants.WiFi_NUM; i++) {

                int located_check = 0;

                while (located_check == 0) {//WiFi APが同じ場所に配置されないようにする
                    located_area = rn.nextInt(Constants.AP_BS_POSITION_NUM); //WiFi APを置く位置を乱数で決定

                    if (lte_deploy_check[located_area] != 1 && deploy_check[located_area] != 1) {

                        wifi_ap[i].located_area_id = located_area;

                        for (int a = 0; a < ap_cover_num; a++) {
                            if (ap_position_cover_area[located_area][a] != -1) {
                                wifi_ap[i].cover_area_list.add(ap_position_cover_area[located_area][a]); //APがカバーしているエリアをセット
                                area_coverd_set[ap_position_cover_area[located_area][a]].add(i); //最小エリアをカバーしているAPをセット
                            }
                        }

                        located_check = 1;
                        deploy_check[located_area] = 1;
                    }
                }
            }

            //すべてのエリアをWiFiでカバーされているかを確認
            int[] wifi_check = new int[Constants.AREA_NUM];
            int index;
            for (int a = 0; a < Constants.AREA_NUM; a++) {//最小エリアがAPでカバーされているかをチェック
                if (area_coverd_set[a].size() != 0) {
                    wifi_check[a] = 1;
                }
            }

            //wifi_check=0の場所を知りたい
            for (int i = 0; i < Constants.AREA_NUM; i++) {
                if (wifi_check[i] == 0) {
                    wifi_ap_set = 0;

                    //すべてのエリアがWiFiでカバーされていない場合、いったんクリア
                    for (int j = 0; j < Constants.WiFi_NUM; j++) {
                        for (int b = 0; b < ap_bs_position_num; b++) {
                            for (int a = 0; a < ap_cover_num; a++) {
                                if (ap_position_cover_area[b][a] != -1) {
                                    wifi_ap[j].cover_area_list.clear(); //APがカバーしているエリアをクリア
                                    area_coverd_set[ap_position_cover_area[b][a]].clear(); //最小エリアをカバーしているAPをクリア
                                }
                            }
                        }
                    }

                    break;
                }
            }
        }
        /*WiFi APの作成終了*/

        /* AP, BSごとに重複しているAP,BSの情報を取得 */
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
//            lteu_bs[i].overray_list = getOverrayAPBSsforLTEU(i); //エリアを見て干渉を決める場合
            lteu_bs[i].overray_list = getCoAPBSoverray(i);  //AP,bs同士で干渉を見る場合
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
//            wifi_ap[i].overray_list = getOverrayAPBSsforWiFi(i); //エリアを見て干渉を決める場合
            wifi_ap[i].overray_list = getCoAPoverray(i);    //AP,bs同士で干渉を見る場合
        }

        /*チャネル干渉の情報を取得＆各AP,BSのキャパシティをセット*/
        CheckInterference ci = new CheckInterference(this);
        for (int i = 0; i < Constants.LTEU_NUM; i++) {
            lteu_bs[i].interference_list = ci.LTEUCheck(i);
            lteu_bs[i].SetCapacity();
        }

        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            wifi_ap[i].interference_list = ci.WiFiCheck(i);
            wifi_ap[i].SetCapacity();
        }

    }

    //AreaTopologyクラスからtopology情報(csvの内容)を取得
    private void getTopology(AreaTopology topology) {
        ap_bs_position_num = topology.ap_bs_position_num;
        ap_cover_num = topology.ap_cover_num;
        ap_position_cover_area = topology.bs_position_cover_area;
        area_covered_ap_num = topology.area_covered_ap_num;
        area_ap_cover = topology.area_ap_cover;

        bs_cover_num = topology.bs_cover_num;
        bs_position_cover_area = topology.bs_position_cover_area;
        area_covered_bs_num = topology.area_covered_bs_num;
        area_bs_cover = topology.area_bs_cover;
        lteu_place = topology.lteu_place;
        lteu_channel_assign = topology.lteu_channel_assign;
    }

    /*重複しているAP,BSのリストを作成(カバーしているエリア自体が重なっていたら干渉とみなす)*/
    private ArrayList getOverrayAPBSsforWiFi(int wifi_id) {
        int target_area;
        PriorityQueue<Integer> temp_aps = new PriorityQueue<>();
        ArrayList<Integer> overrayAPBSs = new ArrayList<>();

        //カバーしているすべてのエリアを探索する
        for (int i = 0; i < wifi_ap[wifi_id].cover_area_list.size(); i++) {
            target_area = wifi_ap[wifi_id].cover_area_list.get(i);

            //一時的重複しているAPを取得(おそらく同じ数字が入っている)
            for (int j = 0; j < area_coverd_set[target_area].size(); j++) {
                temp_aps.add(area_coverd_set[target_area].get(j));
            }

            //一時的に重複しているBSを取得
            for (int j = 0; j < bs_area_coverd_set[target_area].size(); j++) {
                temp_aps.add(bs_area_coverd_set[target_area].get(j) + 10000);
            }
        }

        //番号の重複をなくす
        int index = -1;
        while (!temp_aps.isEmpty()) {
            if (index != temp_aps.peek()) {
                index = temp_aps.poll();
                overrayAPBSs.add(index);
            } else {
                temp_aps.poll();
            }
        }

        for (int i = 0; i < overrayAPBSs.size(); i++) {
            if (overrayAPBSs.get(i) == wifi_id) {
                overrayAPBSs.remove(i);
            }
        }

        return overrayAPBSs;
    }

    /*重複しているAP,BSのリストを作成(カバーしているエリア自体が重なっていたら干渉とみなす)*/
    private ArrayList getOverrayAPBSsforLTEU(int lteu_id) {
        int target_area;
        PriorityQueue<Integer> temp_aps = new PriorityQueue<>();
        ArrayList<Integer> overrayAPBSs = new ArrayList<>();

        //カバーしているすべてのエリアを探索する
        for (int i = 0; i < lteu_bs[lteu_id].cover_area_list.size(); i++) {
            target_area = lteu_bs[lteu_id].cover_area_list.get(i);

            //一時的重複しているAPを取得(おそらく同じ数字が入っている)
            for (int j = 0; j < area_coverd_set[target_area].size(); j++) {
                temp_aps.add(area_coverd_set[target_area].get(j));
            }

            //一時的に重複しているBSを取得
            for (int j = 0; j < bs_area_coverd_set[target_area].size(); j++) {
                temp_aps.add(bs_area_coverd_set[target_area].get(j) + 10000);
            }
        }

        //番号の重複をなくす
        int index = -1;
        while (!temp_aps.isEmpty()) {
            if (index != temp_aps.peek()) {
                index = temp_aps.poll();
                overrayAPBSs.add(index);
            } else {
                temp_aps.poll();
            }
        }

        for (int i = 0; i < overrayAPBSs.size(); i++) {
            if (overrayAPBSs.get(i) == (lteu_id + 10000)) {
                overrayAPBSs.remove(i);
            }
        }
        return overrayAPBSs;
    }

    //WiFiのみユーザが接続可能なAPの情報を収集する
    public LinkedList getAPcovered(int area) {
        LinkedList list = new LinkedList<>();
        for (int i = 0; i < area_coverd_set[area].size(); i++) {
            for (int k = 0; k < Constants.WiFi_CSMA_RANGE; k++) {
                if (ap_position_cover_area[wifi_ap[area_coverd_set[area].get(i)].located_area_id][k] == area) {
                    list.add(area_coverd_set[area].get(i));
                    break;
                }
            }
        }

        return list;
    }

    //WiFi + LTEUユーザが接続可能なAP,BSの情報を収集する
    public LinkedList getAPandBScovered(int area) {
        LinkedList list = new LinkedList<>();

        for (int i = 0; i < area_coverd_set[area].size(); i++) {
            for (int k = 0; k < Constants.WiFi_CSMA_RANGE; k++) {
                if (ap_position_cover_area[wifi_ap[area_coverd_set[area].get(i)].located_area_id][k] == area) {
                    list.add(area_coverd_set[area].get(i));
                    break;
                }
            }
        }

        for (int j = 0; j < bs_area_coverd_set[area].size(); j++) {
            for (int k = 0; k < Constants.LTEU_LBT_RANGE; k++) {
                if (bs_position_cover_area[lteu_bs[bs_area_coverd_set[area].get(j)].located_area_id][k] == area) {
                    list.add(bs_area_coverd_set[area].get(j) + 10000);
                    break;
                }
            }

        }

        return list;

    }

    public WiFiAP[] getWiFiAP() {
        return wifi_ap;
    }

    public LTEUBS[] getLTEUBS() {
        return lteu_bs;
    }

    public WiFiAP getWiFiAP(int index) {
        return wifi_ap[index];
    }

    public LTEUBS getLTEUBS(int index) {
        return lteu_bs[index];
    }

    public ArrayList getAPscoveringArea(int area) {
        return area_coverd_set[area];
    }

    public ArrayList<Integer>[] getAreaCooveredAPSet() {
        return area_coverd_set;
    }

    public ArrayList<Integer>[] getAreaCooveredBSSet() {
        return bs_area_coverd_set;
    }

    public void CopyLTEUBS(LTEUBS[] lteu) {
        lteu_bs = lteu;
    }

    public void CopyWiFiAP(WiFiAP[] wifi) {
        wifi_ap = wifi;
    }

    /* 重複しているAP,BSのリストを作成:AP同士の干渉の場合(隠れ端末問題を無視)⇒AP用 */
    private ArrayList getCoAPoverray(int ap_id) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        ArrayList<Integer> overray = new ArrayList<>();
        int temp_id;
        for (int i = 0; i < Constants.AP_COVER_NUM; i++) {
            if (ap_position_cover_area[wifi_ap[ap_id].located_area_id][i] != -1) {
                for (int j = 0; j < area_coverd_set[ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]].size(); j++) {
                    temp_id = area_coverd_set[ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]].get(j);
                    for (int k = 0; k < 6; k++) {

                        if (ap_position_cover_area[wifi_ap[temp_id].located_area_id][k] == ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]) {

                            pq.add(temp_id);

                        }
                    }
                }
            }
        }

        for (int i = 0; i < Constants.AP_COVER_NUM; i++) {
            if (ap_position_cover_area[wifi_ap[ap_id].located_area_id][i] != -1) {
                for (int j = 0; j < bs_area_coverd_set[ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]].size(); j++) {
                    temp_id = bs_area_coverd_set[ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]].get(j);
                    for (int k = 0; k < 6; k++) {

                        if (bs_position_cover_area[lteu_bs[temp_id].located_area_id][k] == ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]) {
                            pq.add(temp_id + 10000);

                        }
                    }
                }
            }
        }

        int index = -1;
        while (!pq.isEmpty()) {

            if (pq.peek() != index) {
                index = pq.poll();
                if (ap_id != index) {
                    overray.add(index);
                }
            } else {
                pq.poll();
            }
        }

//        //BSは重複して配置されないと想定
//        for (int i = 0; i < Constants.AP_COVER_NUM; i++) {
//            for(int k=0; k < 6; k++){
//                if (ap_position_cover_area[wifi_ap[ap_id].located_area_id][i] != -1 && bs_area_coverd_set[ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]].size() != 0) {
//                    overray.add(bs_area_coverd_set[ap_position_cover_area[wifi_ap[ap_id].located_area_id][i]].get(0) + 10000);
//                    break;
//                }
//            }
//        }
        return overray;

    }

    /* 重複しているAP,BSのリストを作成:APBS同士の干渉の場合(隠れ端末問題を無視)⇒BS用 */
    private ArrayList getCoAPBSoverray(int ap_id) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        ArrayList<Integer> overray = new ArrayList<>();
        int temp_id;
        for (int i = 0; i < Constants.BS_COVER_NUM; i++) {
            if (bs_position_cover_area[lteu_bs[ap_id].located_area_id][i] != -1) {
                for (int j = 0; j < area_coverd_set[bs_position_cover_area[lteu_bs[ap_id].located_area_id][i]].size(); j++) {
                    temp_id = area_coverd_set[bs_position_cover_area[lteu_bs[ap_id].located_area_id][i]].get(j);
                    for (int k = 0; k < 6; k++) {
                        if (ap_position_cover_area[wifi_ap[temp_id].located_area_id][k] == bs_position_cover_area[lteu_bs[ap_id].located_area_id][i]) {
                            pq.add(temp_id);
                        }
                    }
                }
            }
        }

        int index = -1;
        while (!pq.isEmpty()) {

            if (pq.peek() != index) {
                index = pq.poll();
//                if (ap_id != index) {
                overray.add(index); //System.out.println(index + "\t" + wifi_ap[index].located_area_id);
//                }
            } else {
                pq.poll();
            }
        }
        return overray;
    }

}
