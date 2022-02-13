package com.lilium.snake.laaandwifisimulator;

import com.lilium.snake.laaandwifisimulator.network.ActionChannel;
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

    public Simulator(UserParameter param) {
        _param = param;

        Constants.CAPACITY_WITH_LAA_WIFI = Utility.SetCapacitySharedWiFiLTEU();
        Constants.CAPACITY_WITH_WIFIS = Utility.SetCapacitySharedWiFi();
        Constants.SERVICE_SET = _param.service_set;
        Constants.are = _param.wifi_user_lambda + "";
        initialize();
    }

    public WifiState initialize() {
        acctionCounter = 0;
        try {
            _topology = new AreaTopology();
            changeChannelOfStation(null);
            ave_throughput = _scenario.getData().ave_throughput;
        } catch (Exception e) {
            System.out.println("Init hiij chadsangui " + e.getMessage());
        }
        return getObservation();
    }

    public double changeChannelOfStation(ActionChannel action) {
        // if (action != null)
        // System.out.println(action.id + "," + action.channel);
        acctionCounter++;
        try {
            _scenario = new Scenario(30, _param, _topology);
            _scenario.startSimulationDQN(action);
        } catch (IOException e) {

            e.printStackTrace();
        }
        double tr = _scenario.getData().ave_throughput;
        if (ave_throughput < tr) {
            ave_throughput = tr;
            return 10;
        } else if (ave_throughput == tr) {
            return 0;
        }
        return -1;
    }

    private double[] _observation = new double[(Constants.WiFi_NUM + Constants.LTEU_NUM) * 7];

    public WifiState getObservation() {

        for (int i = 0; i < _scenario.getArea().getWiFiAP().length; i++) {
            int num = i * 7;
            _observation[num + 0] = _scenario.getArea().getWiFiAP()[i].getAp_id();
            _observation[num + 1] = _scenario.getArea().getWiFiAP()[i].getConnecting_num();
            _observation[num + 2] = _scenario.getArea().getWiFiAP()[i].getCapacity();
            _observation[num + 3] = _scenario.getArea().getWiFiAP()[i].getMax_capacity();
            _observation[num + 4] = _scenario.getArea().getWiFiAP()[i].getLocated_area_id();
            _observation[num + 5] = _scenario.getArea().getWiFiAP()[i].getAssigned_channel();
            _observation[num + 6] = _scenario.getArea().getWiFiAP()[i].getUser_throughput();
        }
        for (int i = _scenario.getArea().getWiFiAP().length; i < _scenario.getArea().getWiFiAP().length
                + _scenario.getArea().getLTEUBS().length; i++) {
            int num = i * 7;
            _observation[num + 0] = _scenario.getArea().getLTEUBS()[i - _scenario.getArea().getWiFiAP().length]
                    .getAp_id();
            _observation[num + 1] = _scenario.getArea().getLTEUBS()[i - _scenario.getArea().getWiFiAP().length]
                    .getConnecting_num();
            _observation[num + 2] = _scenario.getArea().getLTEUBS()[i - _scenario.getArea().getWiFiAP().length]
                    .getCapacity();
            _observation[num + 3] = _scenario.getArea().getLTEUBS()[i - _scenario.getArea().getWiFiAP().length]
                    .getMax_capacity();
            _observation[num + 4] = _scenario.getArea().getLTEUBS()[i - _scenario.getArea().getWiFiAP().length]
                    .getLocated_area_id();
            _observation[num + 5] = _scenario.getArea().getLTEUBS()[i - _scenario.getArea().getWiFiAP().length]
                    .getAssigned_channel();
            _observation[num + 6] = _scenario.getArea().getLTEUBS()[i - _scenario.getArea().getWiFiAP().length]
                    .getUser_throughput();
        }

        return new WifiState(_observation);
    }

    public boolean isOngoing() {
        return acctionCounter < (Constants.WiFi_NUM + Constants.LTEU_NUM) * 4;
    }
}
