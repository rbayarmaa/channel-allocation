package com.lilium.snake.laaandwifisimulator;

import com.lilium.snake.laaandwifisimulator.network.ActionChannel;
import com.lilium.snake.laaandwifisimulator.network.Environment;
import com.lilium.snake.laaandwifisimulator.network.WiFiNetworkUtil;
import com.lilium.snake.laaandwifisimulator.sumulator.*;
import com.lilium.snake.network.util.GameStateUtil;

import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
// import java.util.List;

public class MainWiFi {
    private static final Logger LOG = LoggerFactory.getLogger(MainWiFi.class);

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();// 時間計
        MainWiFi wifi = new MainWiFi();
        long end = System.currentTimeMillis();
        System.out.println("this is END Simulation Time: " + (end - start) + "[ms]");
    }

    final Simulator game;

    public MainWiFi() {
        String[] args1 = { "1", "300", "0.0005", "0.0005", "0", "650", "1", "1", "1000", "5", "10", "3", "1" };
        game = new Simulator(new UserParameter(args1));

        final String randomNetworkName = "network-" + System.currentTimeMillis() + ".zip";

        // Create our training environment
        final Environment mdp = new Environment(game);
        final QLearningDiscreteDense<WifiState> dql = new QLearningDiscreteDense<>(
                mdp,
                WiFiNetworkUtil.buildDQNFactory(),
                WiFiNetworkUtil.buildConfig());

        // Start the training
        dql.train();
        mdp.close();

        // Save network
        try {
            dql.getNeuralNet().save(randomNetworkName);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        // Reset the game
        game.initialize();

        // Evaluate just trained network
        evaluateNetwork(game, randomNetworkName);

    }

    private void evaluateNetwork(Simulator game, String randomNetworkName) {
        final MultiLayerNetwork multiLayerNetwork = WiFiNetworkUtil.loadNetwork(randomNetworkName);
        double high_score = 0;
        for (int i = 0; i < 1000; i++) {
            double score = 0;
            while (game.isOngoing()) {
                try {
                    final WifiState state = game.getObservation();
                    final INDArray output = multiLayerNetwork.output(state.getMatrix(), false);
                    double[] data = output.data().asDouble();
                    int maxValueIndex = GameStateUtil.getMaxValueIndex(data);
                    var action = new ActionChannel(maxValueIndex);

                    score = game.changeChannelOfStation(action);

                    // Needed so that we can see easier what is the game doing
                    WiFiNetworkUtil.waitMs(0);
                } catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                    // game.endGame();
                }
            }

            LOG.info("Score of iteration '{}' was '{}'", i, score);
            if (score > high_score) {
                high_score = score;
            }

            // Reset the game
            game.initialize();
        }
        LOG.info("Finished evaluation of the network, high_score was '{}'", high_score);
    }

}
