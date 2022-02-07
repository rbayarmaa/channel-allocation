package com.lilium.snake.laaandwifisimulator;

import com.lilium.snake.laaandwifisimulator.sumulator.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
// import java.util.List;

public class MainWiFi {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();// 時間計
        String[] args1 = { "1", "300", "0.0005", "0.0005", "0", "650", "1", "1", "1000", "5", "10", "3", "1" };
        MainWiFi wifi = new MainWiFi(new UserParameter(args1));

        long end = System.currentTimeMillis();
        System.out.println("this is END Simulation Time: " + (end - start) + "[ms]");
    }

    private UserParameter _param;
    private AreaTopology _topology;
    private Scenario _scenario;

    public MainWiFi(UserParameter param) {
        _param = param;
        _topology = new AreaTopology();
        Constants.CAPACITY_WITH_LAA_WIFI = Utility.SetCapacitySharedWiFiLTEU();
        Constants.CAPACITY_WITH_WIFIS = Utility.SetCapacitySharedWiFi();
        Constants.SERVICE_SET = _param.service_set;
        Constants.are = _param.wifi_user_lambda + "";

    }

}
