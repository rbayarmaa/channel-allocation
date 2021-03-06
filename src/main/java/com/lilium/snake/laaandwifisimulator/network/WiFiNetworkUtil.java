package com.lilium.snake.laaandwifisimulator.network;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.nd4j.linalg.learning.config.RmsProp;

import java.io.File;
import java.io.IOException;

import com.lilium.snake.laaandwifisimulator.sumulator.Constants;

/**
 * Util class containing methods to build the neural network and its
 * configuration.
 *
 * @author mirza
 */
public final class WiFiNetworkUtil {
    /**
     * Number of neural network inputs.
     */
    public static final int NUMBER_OF_INPUTS = (Constants.WiFi_NUM + Constants.LTEU_NUM) * 7;
    /**
     * end observation-ii max, min utgiig todorhoilj uguh shaardlagagui yu? Jishee ni APID=0 or 107
     */
    private WiFiNetworkUtil() {
    }

    public static QLearningConfiguration buildConfig() {
        return QLearningConfiguration.builder()
                .seed((long) ((Constants.WiFi_NUM + Constants.LTEU_NUM) * 4))
                .maxEpochStep(Constants.WiFi_NUM + Constants.LTEU_NUM)
                .maxStep(15000)
                .expRepMaxSize(150000)
                .batchSize(32)
                .targetDqnUpdateFreq(500)
                .updateStart(10)
                .rewardFactor(0.01)
                .gamma(0.99)
                .errorClamp(1.0)
                .minEpsilon(0.1f)
                .epsilonNbStep(1000)
                .doubleDQN(true)
                .build();
    }

    public static DQNFactoryStdDense buildDQNFactory() {
        final DQNDenseNetworkConfiguration build = DQNDenseNetworkConfiguration.builder()
                .l2(0.001)
                .updater(new RmsProp(0.00025)) //0.000025 bsan
                .numHiddenNodes(300)
                .numLayers(2)
                .build();

        return new DQNFactoryStdDense(build);
    }

    public static MultiLayerNetwork loadNetwork(final String networkName) {
        try {
            return MultiLayerNetwork.load(new File(networkName), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Used to slow down game step so that the user can see what is happening.
     *
     * @param ms Number of milliseconds for how long the thread should sleep.
     */
    public static void waitMs(final long ms) {
        if (ms == 0) {
            return;
        }

        try {
            Thread.sleep(ms);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
