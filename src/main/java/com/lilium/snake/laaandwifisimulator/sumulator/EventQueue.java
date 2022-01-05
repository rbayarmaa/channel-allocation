/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lilium.snake.laaandwifisimulator.sumulator;

import java.util.ArrayList;

/**
 * イベントの事象表
 *
 * @author ginnan
 */
public class EventQueue {
    private static ArrayList<Event> eventQ;
    private static int event_id;
    private static int node_id;
    private static int wifi_node_id;
    private static int lteu_node_id;

    private double current_time;     // 現在時刻

    public int getNextEventID() {
        event_id++;
        return event_id;
    }

    public int getNextNodeID() {//ユーザID(WiFi,LTE-U関係なし)
        node_id++;
        return node_id;
    }

    public int getNextWiFiNodeID() {//WiFiのみユーザのID
        wifi_node_id++;
        return wifi_node_id;
    }

    public int getNextLTEUNodeID() {//WiF + LTE-UユーザのID
        lteu_node_id++;
        return lteu_node_id;
    }

    public double getCurrentTime() {
        return current_time;
    }

    public void add(Event event) {
        eventQ.add(event);
    }

    public boolean isEmpty() {
        return eventQ.isEmpty();
    }

    /**
     * 次に実行するイベントを返す
     *
     * @return 次に実行するイベント
     */
    public Event pop() {
        int minID = 0;
        double minVal = eventQ.get(0).getTime();
        for (int i = 1; i < eventQ.size(); i++) {
            if (minVal > eventQ.get(i).getTime()) {
                minVal = eventQ.get(i).getTime();
                minID = i;
            }
        }
        Event event = eventQ.remove(minID);
        current_time = event.getTime();
        return event;
    }

    /**
     * キューの内容を消す
     */
    public void cleanEventQueue() {
        Event event;
        while (!isEmpty()) {
            event = eventQ.remove(0);
            event.finish();
            event = null;
        }
    }

    public EventQueue() {
        eventQ = new ArrayList<Event>();
        event_id = 0;
        node_id = 0;
        wifi_node_id = 0;
        lteu_node_id = 0;
        current_time = 0.0;
    }
}
