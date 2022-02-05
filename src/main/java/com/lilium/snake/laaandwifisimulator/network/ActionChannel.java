package com.lilium.snake.laaandwifisimulator.network;

public class ActionChannel {
    int wifi_id;
    int channel;

    ActionChannel(int action) {
        this.wifi_id = action / 4;
        this.channel = action % 4;

    }
}
