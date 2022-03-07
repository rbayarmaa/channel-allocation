package com.lilium.snake.laaandwifisimulator;

import com.lilium.snake.laaandwifisimulator.network.ActionChannel;
import com.lilium.snake.laaandwifisimulator.network.WiFiNetworkUtil;
import com.lilium.snake.laaandwifisimulator.sumulator.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private UserParameter _param;
    private AreaTopology _topology;
    private Scenario _scenario;
    private int acctionCounter;
    private double ave_throughput;
    private double max_ave_throughput;

    public Simulator(UserParameter param) {
        _param = param;

        Constants.CAPACITY_WITH_LAA_WIFI = Utility.SetCapacitySharedWiFiLTEU();
        Constants.CAPACITY_WITH_WIFIS = Utility.SetCapacitySharedWiFi();
        Constants.SERVICE_SET = _param.service_set;
        Constants.are = _param.wifi_user_lambda + "";
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WifiState initialize() throws IOException {
        acctionCounter = 0;
        try {
            _topology = new AreaTopology();
            _scenario = new Scenario(30, _param, _topology);

            System.out.println(
                    "------> init ave_throughput: " + ave_throughput + " max_ave_throughput:" + max_ave_throughput);
            ave_throughput = _scenario.getData().ave_throughput;

        } catch (Exception e) {
            System.out.println("Init hiij chadsangui " + e.getMessage());
        }
        return getObservation();
    }

    public double changeChannelOfStation(int[] action) {
        // if (action != null)
        // System.out.println(action.id + "," + action.channel);
        acctionCounter++;
        try {
            // TODO Make ovservation sesition jisheen observationii utgiic avtionDesitiontei
            // jil bolgood bugendeerh utgiig uguud sesitioniigni hargah
            _scenario = new Scenario(30, _param, _topology);
            _scenario.startSimulationDQN(action);
        } catch (IOException e) {

            e.printStackTrace();
        }
        double tr = _scenario.getData().ave_throughput;
        int ret = -1;
        if (ave_throughput < tr) {
            ave_throughput = tr;
            ret = 1;
        } else if (ave_throughput == tr) {
            ret = 0;
        }
        if (max_ave_throughput < ave_throughput) {
            max_ave_throughput = ave_throughput;
            ret = 100;
        }
        return ret;
    }

    public WifiState getObservation() throws IOException {
        double[] _observation = new double[(Constants.WiFi_NUM + Constants.LTEU_NUM) * 4];

        for (int i = 0; i < ((Constants.WiFi_NUM + Constants.LTEU_NUM) * 4); i++) {
            _scenario.startSimulationDQN(ActionChannel.getCurrentAction(i));
            _observation[i] = _scenario.getData().ave_throughput;
        }
        return new WifiState(_observation);
    }

    public boolean isOngoing() {
        return acctionCounter < (Constants.WiFi_NUM + Constants.LTEU_NUM) * 4;
    }
}
