/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.io.IOException;

/**
 * ユーザの通信終了イベント
 *
 * @author ginnan
 */
public class EventOfUserEnd extends Event {

    public EventOfUserEnd(UserNode user, Scenario scenario) {
        super(scenario);
        event_time = user.getEndTime();
        this.event_user = user;
    }

    @Override
    public void runEvent() throws IOException {
        commEndProcess();
    }

    private void commEndProcess() throws IOException {

        if (_param.service_set == 1) {// 一定時間通信をする場合の処理. ユーザの平均スループットを計算する
            event_user.CalcAverageThroughput2();
            event_user.CalcProvidedDataSize();
            // event_user.CalcMinThroghputTimeAverage();
            // event_user.CalcMinThroghputTime();
        }

        // アクセスポイントにおける通信終了の処理
        event_user.getConnectedAP().commEndProcess(event_user, _queue.getCurrentTime(), _param.service_set);

        /* 最小スループットの時間平均の処理 */
        _scenario.gettimeMinData(event_time);

        if (_param.end_condition == 1) {/* シミュレーション終了条件が時間 */
            // 書いていないので、需要あれば記述してください.

        } else {/* シミュレーション終了条件が呼数 */
            if (event_user.user_set == 0) {// WiFiのみユーザの場合
                // 統計対象の呼なら、統計情報を更新
                if (event_user.wifi_node_id < _param.end_num + Constants.STEADY_NUM
                        && event_user.wifi_node_id >= Constants.STEADY_NUM) {
                    int target_end = _scenario.getData().updateTargetUser(event_user);
                    // シミュレーション終了条件の確認
                    if (_scenario.getData().target_lteu_end_num == _param.end_num && target_end == _param.end_num) {
                        Event sim_event = new EventOfSimEnd(_scenario);
                        _queue.add(sim_event);
                    }

                    /// WiFiのみユーザだけ到着する場合の終了条件 20180117 add
                    if (_scenario.getUserParameter().lteu_user_lambda == 0 && target_end == _param.end_num) {
                        Event sim_event = new EventOfSimEnd(_scenario);
                        _queue.add(sim_event);
                    }
                    /// WiFiのみユーザだけ到着する場合の終了条件 add end

                }
                // システム飽和時のシミュレーション終了条件
                if (_param.service_set == 0 && event_user.wifi_node_id >= (_param.end_num * 5) + Constants.STEADY_NUM) {
                    System.out.println("WiFi_Saturation" + event_user.wifi_node_id);
                    Event sim_event = new EventOfSimEnd(_scenario);
                    _queue.add(sim_event);
                }

            } else {// WiFi + LTE-Uユーザのみの場合
                if (event_user.lteu_node_id < _param.end_num + Constants.STEADY_NUM
                        && event_user.lteu_node_id >= Constants.STEADY_NUM) {
                    int target_end = _scenario.getData().updateTargetUser(event_user);

                    if (_scenario.getData().target_wifi_end_num == _param.end_num && target_end == _param.end_num) {// System.out.println("LTE_end"
                                                                                                                    // +
                                                                                                                    // event_user.lteu_node_id);
                        Event sim_event = new EventOfSimEnd(_scenario);
                        _queue.add(sim_event);
                    }

                    /// LTE+WiFiユーザだけ到着する場合の終了条件 20180117 add
                    if (_scenario.getUserParameter().wifi_user_lambda == 0 && target_end == _param.end_num) {// System.out.println("WiFi_end"
                                                                                                             // +
                                                                                                             // event_user.wifi_node_id);
                        Event sim_event = new EventOfSimEnd(_scenario);
                        _queue.add(sim_event);
                    }
                    /// LTE+WiFiユーザだけ到着する場合の終了条件 add end

                }

                // システム飽和時のシミュレーション終了条件
                if (_param.service_set == 0 && event_user.lteu_node_id >= (_param.end_num * 5) + Constants.STEADY_NUM) {
                    System.out.println("LTE_Saturation" + event_user.lteu_node_id);
                    Event sim_event = new EventOfSimEnd(_scenario);
                    _queue.add(sim_event);
                }
            }
        }
    }
}
