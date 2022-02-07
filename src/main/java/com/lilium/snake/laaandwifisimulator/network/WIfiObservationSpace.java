package com.lilium.snake.laaandwifisimulator.network;

import com.lilium.snake.laaandwifisimulator.WifiState;
import com.lilium.snake.laaandwifisimulator.sumulator.Constants;

import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class WIfiObservationSpace implements ObservationSpace<WifiState> {
    private static final double[] LOWS = WIfiObservationSpace.createValueArray(new double[] { 0, 0, 0, 0, 0, 0, 0 });
    private static final double[] HIGHS = WIfiObservationSpace
            .createValueArray(new double[] { 10100, Constants.LTEU_CAPACITY, Constants.LTEU_CAPACITY,
                    Constants.AREA_NUM, Constants.CHANNEL_NUM, 1000000 });

    @Override
    public String getName() {
        return "WIfiObservationSpace";
    }

    @Override
    public int[] getShape() {
        return new int[] {
                1, WiFiNetworkUtil.NUMBER_OF_INPUTS
        };
    }

    @Override
    public INDArray getLow() {
        return Nd4j.create(LOWS);
    }

    @Override
    public INDArray getHigh() {
        return Nd4j.create(HIGHS);
    }

    private static double[] createValueArray(final double[] value) {
        final double[] values = new double[WiFiNetworkUtil.NUMBER_OF_INPUTS];
        for (int i = 0; i < WiFiNetworkUtil.NUMBER_OF_INPUTS; i++) {
            values[i] = value[i % value.length];
        }
        return values;
    }
}
