/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * シミュレーションシナリオのクラス
 *
 * @author ginnan
 */
public class Scenario {

    private final UserParameter _param;
    private final EventQueue _queue;
    private final MersenneTwister _rnd;
    private Area _area;
    private final DataOfOneSim _data;

    //GAの個体を保存するやつ 20180105追加
    private LinkedList<int[][]> individuals;  //[][0:BS,APのID, 1:割り当てチャネル]
    private boolean first_flag; //初期集団を生成するか、受け継ぐかのフラグ

    public void startSimulation() throws IOException {

        //各最小エリアで、WiFiのみユーザ, WiFi + LTEUユーザの最初の発生
        createFirstWiFiUserArrivalEvents();
        createFirstLTEUUserArrivalEvents();

//        createFirstWiFiUserArrivalEvents2();
//        createFirstLTEUUserArrivalEvents2();
        /*提案手法,比較手法の最初の発生イベント */
        creatFirstEventOfGenericAlgorithm6();

        while (!_queue.isEmpty()) {
            //イベントキューからイベントを取り出して実行
            Event sim_event = _queue.pop();
            sim_event.runEvent();
            sim_event.finish();
        }
        _data.loopEnd();
    }

    //WiFiユーザの最初の到着イベント
    private void createFirstWiFiUserArrivalEvents() {

        for (int area = 0; area < Constants.AREA_NUM; area++) {

            double time = _queue.getCurrentTime() + Utility.expRand(_param.wifi_user_lambda, _rnd);

            UserNode user = new UserNode(area, _queue.getNextNodeID(), time, 0, _area, _queue.getNextWiFiNodeID());
            /*次のイベントを作成*/
            Event next_event = new EventOfCreateWiFiuser(time, user, this);
            _queue.add(next_event);
        }
    }

    //LTEユーザの最初の到着イベント
    private void createFirstLTEUUserArrivalEvents() {

        for (int area = 0; area < Constants.AREA_NUM; area++) {

            double time = _queue.getCurrentTime() + Utility.expRand(_param.lteu_user_lambda, _rnd);
            UserNode user = new UserNode(area, _queue.getNextNodeID(), time, 1, _area, _queue.getNextLTEUNodeID());
            /*次のイベントを作成*/
            Event next_event = new EventOfCreateLTEUuser(time, user, this);
            _queue.add(next_event);

        }
    }

    /* LTE-U+WiFiユーザがLTE-Uがあるエリアにのみ到着する場合 (使っていない)*/
    private void createFirstWiFiUserArrivalEvents2() {
        ArrayList<Integer>[] cover_or = _area.getAreaCooveredBSSet();// ***LTE-Uのカバーエリアのみに到着する場合の操作
        for (int area = 0; area < Constants.AREA_NUM; area++) {
            double time; //*****
            if (cover_or[area].size() != 0) {//*******
                time = _queue.getCurrentTime() + Utility.expRand(_param.wifi_user_lambda / 2, _rnd);//********

            } else {//********
                time = _queue.getCurrentTime() + Utility.expRand(_param.wifi_user_lambda, _rnd);

            }//*******
            UserNode user = new UserNode(area, _queue.getNextNodeID(), time, 0, _area, _queue.getNextWiFiNodeID());
            /*次のイベントを作成*/
            Event next_event = new EventOfCreateWiFiuser(time, user, this);
            _queue.add(next_event);
        }
    }

    /* LTE-U+WiFiユーザがLTE-Uがあるエリアにのみ到着する場合 (使っていない)*/
    private void createFirstLTEUUserArrivalEvents2() {
        ArrayList<Integer>[] cover_or = _area.getAreaCooveredBSSet();// ***LTE-Uのカバーエリアのみに到着する場合の操作
        for (int area = 0; area < Constants.AREA_NUM; area++) {
            if (cover_or[area].size() != 0) {//*******
                double time = _queue.getCurrentTime() + Utility.expRand(_param.lteu_user_lambda, _rnd);
                UserNode user = new UserNode(area, _queue.getNextNodeID(), time, 1, _area, _queue.getNextLTEUNodeID());
                /*次のイベントを作成*/
                Event next_event = new EventOfCreateLTEUuser(time, user, this);
                _queue.add(next_event);
            }//******
        }
    }

    private void creatFirstEventOfProposedMethod() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfConnectionChange() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfProposedConnection(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfBruteForce() {
//        double time = _queue.getCurrentTime() + _param.interval_time;
//        Event next_event = new EventOfBruteForce(time, this);
        Event next_event = new EventOfBruteForce(_queue.getCurrentTime(), this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm2() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm2(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm3() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm3(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm4() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm4(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm5() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm5(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm6() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm6(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm7() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm7(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm8() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm8(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfGenericAlgorithm9() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfGenericAlgorithm9(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfCAonly() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfCAonly(time, this);
        _queue.add(next_event);
    }

    private void creatFirstEventOfDynamicAssign() {
        double time = _queue.getCurrentTime() + _param.interval_time;
        Event next_event = new EventOfDynamicAssign(time, this);
        _queue.add(next_event);
    }

    /* 可能な限りチャネルをoff */
    private void DoChannelOffAsPossible() {
        int area_id;
        int[] channel_overray_check;    //1つのWiFi APに対して、カバーしているエリアごとの重複チャネル数をチェック
        CheckInterference ci = new CheckInterference(_area);

        ArrayList<Integer> cover_areas;
        ArrayList<Integer>[] area_covered_aps = _area.getAreaCooveredAPSet();
//         ArrayList<Integer>[] area_covered_bss = _area.getAreaCooveredBSSet() ;

        //WiFi AP IDを0から見ていって、可能な限りoffにする
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            boolean not_change = false;
            cover_areas = _area.getWiFiAP(i).cover_area_list;
            //カバーエリアを見て、APのチャネルをoffにできるかを判断
            channel_overray_check = new int[cover_areas.size()];
            for (int j = 0; j < cover_areas.size(); j++) {//WiFiのカバーエリアの探索
                area_id = cover_areas.get(j);

                for (int k = 0; k < area_covered_aps[area_id].size(); k++) { //最小エリアごとのAPの探索
                    if (_area.getWiFiAP(area_covered_aps[area_id].get(k)).assigned_channel >= 0) {
                        channel_overray_check[j]++;
                    }
                }
            }
            //channel_overray_checkから、すべてのエリアを確認
            for (int j = 0; j < cover_areas.size(); j++) {
                if (channel_overray_check[j] <= 1) {
                    not_change = true;
                    break;//そのAPではチャネルoffにできない。
                }
            }

            if (!not_change) {//チャネルoffにする
                if (_area.getWiFiAP(i).interference_list.size() >= 1) {//
                    _area.getWiFiAP(i).assigned_channel = -1;
                }//

            }
        }

        ci = new CheckInterference(_area);

        //干渉と容量のセット
        for (int i = 0; i < Constants.WiFi_NUM; i++) {

            if (_area.getWiFiAP(i).assigned_channel != -1) {
                _area.getWiFiAP(i).assigned_channel = ci.WiFiLeastInterference(i, _area.getWiFiAP(), _area.getLTEUBS());
                _area.getWiFiAP(i).interference_list = ci.WiFiCheck(i);
                _area.getWiFiAP(i).SetCapacity();
            } else {
                _area.getWiFiAP(i).interference_list = ci.WiFiCheck(i);
                _area.getWiFiAP(i).SetCapacity();
            }
        }

        for (int j = 0; j < Constants.LTEU_NUM; j++) {
            _area.getLTEUBS(j).interference_list = ci.LTEUCheck(j);
            _area.getLTEUBS(j).SetCapacity();
        }

    }

    /* 隣接チャネルを見て最も干渉が少ないチャネルを割り当て (LTE-Uは変更なし)*/
    private void DoNeighborAssign() {

        CheckInterference ci = new CheckInterference(_area);

        //最初はチャネル未割当に
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _area.getWiFiAP(i).assigned_channel = -1;
        }

//        for(int j = 0; j < Constants.LTEU_NUM; j++){
//            _area.getLTEUBS(j).assigned_channel = -1;
//        }
        //
        //チャネル割り当ての実行
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _area.getWiFiAP(i).assigned_channel = ci.WiFiLeastInterference(i, _area.getWiFiAP(), _area.getLTEUBS());
        }

        //干渉と容量のセット
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _area.getWiFiAP(i).interference_list = ci.WiFiCheck(i);
            _area.getWiFiAP(i).SetCapacity();
        }

        for (int j = 0; j < Constants.LTEU_NUM; j++) {
            _area.getLTEUBS(j).interference_list = ci.LTEUCheck(j);
            _area.getLTEUBS(j).SetCapacity();
        }

//        for(int j = 0; j < Constants.LTEU_NUM; j++){
//            _area.getLTEUBS(j).assigned_channel = -1;
//        }
    }

    /* ランダム割り当てでチャネルoffがあるやつ 20180117 add */
    private void DoRandomAssignWithChannelOff() {
        boolean flag;

        Random rn = new Random();
        CheckInterference ci = new CheckInterference(_area);

        int covered_channel;
        ArrayList<Integer>[] ap_cover_area = _area.getAreaCooveredAPSet();

        do {
            flag = true;
            for (int i = 0; i < Constants.WiFi_NUM; i++) {
                _area.getWiFiAP(i).assigned_channel = rn.nextInt(Constants.CHANNEL_NUM + 1) - 1;
//                System.out.println(_area.getWiFiAP(i).assigned_channel);
            }

            for (int a = 0; a < Constants.AREA_NUM; a++) {
                covered_channel = -1;
                for (int b = 0; b < ap_cover_area[a].size(); b++) {
                    if (_area.getWiFiAP(ap_cover_area[a].get(b)).assigned_channel != -1) {
                        covered_channel = _area.getWiFiAP(ap_cover_area[a].get(b)).assigned_channel;
                    }
                }

                if (covered_channel == -1) {//制約条件を満たさない
                    flag = false;
                }
            }
        } while (!flag);

        //干渉と容量のセット
        for (int i = 0; i < Constants.WiFi_NUM; i++) {
            _area.getWiFiAP(i).interference_list = ci.WiFiCheck(i);
            _area.getWiFiAP(i).SetCapacity();
        }

        for (int j = 0; j < Constants.LTEU_NUM; j++) {
            _area.getLTEUBS(j).interference_list = ci.LTEUCheck(j);
            _area.getLTEUBS(j).SetCapacity();
        }
    }

    /* 全探索により、最初のチャネル割り当てを実行⇒総容量の最大化が目的 */
//    private void DoBrutoForceAssign(){
//         ArrayList<int[][]> patterns = new ArrayList<>();
////        int[][] channel_pattern = new int[Constants.WiFi_NUM][2];//WiFiのみチャネルを変更するため
//        int wifi_id = 0;
//
//        for (int ch = 0; ch < Constants.CHANNEL_NUM; ch++) {
//            RecursionSearch(wifi_id, ch);
//        }
//        
//         //最大の評価値の個体をBS,APに適用し、接続先を変更
//        for (int i = 0; i < Constants.LTEU_NUM; i++) {
//            _area.getLTEUBS(i).assigned_channel = max_pattern[i][1];
//        }
//
//        for (int i = 0; i < Constants.WiFi_NUM; i++) {
//            _area.getWiFiAP(i).assigned_channel = max_pattern[i + Constants.LTEU_NUM][1];
//        }
//
//        //干渉の容量をセット
//        for (int i = 0; i < Constants.LTEU_NUM; i++) {
//            _area.getLTEUBS(i).interference_list = ci.LTEUCheck(i, _area.getWiFiAP(), _area.getLTEUBS());
//            _area.getLTEUBS(i).SetCapacity();
//        }
//
//        for (int i = 0; i < Constants.WiFi_NUM; i++) {
//            _area.getWiFiAP(i).interference_list = ci.WiFiCheck(i, _area.getWiFiAP(), _area.getLTEUBS());
//            _area.getWiFiAP(i).SetCapacity();
//        }
//    }
//    private void RecursionSearch(int wifi_id, int channel) {
//        double evaluation_value;
//        channel_pattern[wifi_id][0] = wifi_id;
//        channel_pattern[wifi_id][1] = channel;
//
//        wifi_id++;
//
//        if (wifi_id < Constants.WiFi_NUM) {
//            for (int ch = 0; ch < Constants.CHANNEL_NUM; ch++) {
//                RecursionSearch(wifi_id, ch);
//            }
//        }
//
//        evaluation_value = Evaluate(channel_pattern);
//
//        if (max_evaluation_value < evaluation_value) {
//            max_pattern = channel_pattern;
//        }
//
//    }
    public UserParameter getUserParameter() {
        return _param;
    }

    public EventQueue getQueue() {
        return _queue;
    }

    public MersenneTwister getRnd() {
        return _rnd;
    }

    public Area getArea() {
        return _area;
    }

    public DataOfOneSim getData() {
        return _data;
    }

    //最小スループットの時間平均を計算するための関数
    public void gettimeMinData(double time) {
        double temp_min_thr_lteu = 99999;
        double temp_min_thr_wifi = 99999;
        double temp_min_thr = 99999;

        for (int x = 0; x < Constants.LTEU_NUM; x++) {
            if (_area.getLTEUBS(x).assigned_channel != -1 && _area.getLTEUBS(x).connecting_num != 0 && _area.getLTEUBS(x).user_throughput > 0 && _area.getLTEUBS(x).user_throughput < temp_min_thr_lteu) {
                temp_min_thr_lteu = _area.getLTEUBS(x).user_throughput;
            }
        }
        _data.updateMintimeAverageLTEU(temp_min_thr_lteu, time);

        for (int x = 0; x < Constants.WiFi_NUM; x++) {
            if (_area.getWiFiAP(x).assigned_channel != -1 && _area.getWiFiAP(x).connecting_num != 0 && _area.getWiFiAP(x).user_throughput > 0 && _area.getWiFiAP(x).user_throughput < temp_min_thr_wifi) {
                temp_min_thr_wifi = _area.getWiFiAP(x).user_throughput;
            }
        }

        _data.updateMintimeAverageWiFi(temp_min_thr_wifi, time);

        if (temp_min_thr_lteu < temp_min_thr_wifi) {
            temp_min_thr = temp_min_thr_lteu;
        } else {
            temp_min_thr = temp_min_thr_wifi;
        }

        _data.updateMintimeAverage(temp_min_thr, time);
    }

    //    //GAの最初の初期集団を作る
//    private void CreateFirstIndividuals(){
//        int[][] temp_individual;
//        
//        for(int a = 0; a < Constants.GA_INDIVIDUAL_NUM; a++){
//            
//        }
//        
//    }
    public boolean getFirstflag() {
        return first_flag;
    }

    public void getFirstflagfalse() {
        first_flag = false;
    }

    //GAの実行終了後、集団の中の個体を取得する
    public LinkedList<int[][]> getIndividuals() {
        return individuals;
    }

    //前回のGAの終了時の個体を次のGAの初期個体にするためのもの
    public void copyIndividuals(LinkedList<int[][]> ind) {
        individuals.clear();
        for (int a = 0; a < ind.size(); a++) {
            individuals.add(ind.get(a));
        }

    }

    public Scenario(int seed, UserParameter param, AreaTopology topology) throws IOException {

        _rnd = new MersenneTwister(seed);
        _param = param;
        _queue = new EventQueue();
        _area = new Area(this, topology);
        _data = new DataOfOneSim(_param);

        individuals = new LinkedList<>();
        first_flag = true;

    }
}
