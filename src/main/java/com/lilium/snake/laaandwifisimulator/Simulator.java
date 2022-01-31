package com.lilium.snake.laaandwifisimulator;

import com.lilium.snake.laaandwifisimulator.sumulator.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private UserParameter _param;
    private AreaTopology _topology;
    private Scenario _scenario;

    public Simulator(UserParameter param) {
        _param = param;
        _topology = new AreaTopology();
        Constants.CAPACITY_WITH_LAA_WIFI = Utility.SetCapacitySharedWiFiLTEU();
        Constants.CAPACITY_WITH_WIFIS = Utility.SetCapacitySharedWiFi();
        Constants.SERVICE_SET = _param.service_set;
        Constants.are = _param.wifi_user_lambda + "";
        initialize();
    }

    public WifiState initialize() {
        try {
            _scenario = new Scenario(30, _param, _topology);
            _scenario.startSimulation();
        } catch (Exception e) {
            System.out.println("Init hiij chadsangui " + e.getMessage());
        }
        return getObservation();
    }

    public WifiState getObservation() {

        ArrayList<Double> state = new ArrayList<Double>();

        for (var w : _scenario.getArea().getWiFiAP())
            state.addAll(w.getObservationState());

        for (var w : _scenario.getArea().getLTEUBS())
            state.addAll(w.getObservationState());

        var ss = state.toArray(new Double[0]);

        return new WifiState(ArrayUtils.toPrimitive(ss));
    }
}
