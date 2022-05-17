package com.lilium.snake.laaandwifisimulator;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class WifiState  implements Encodable {
    private final double[] inputs;

    public WifiState(final double[] inputs) {
        this.inputs = inputs;
    }

    @Override
    public double[] toArray() {
        return inputs;
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return Nd4j.create(inputs);
    }

    public INDArray getMatrix() {
        INDArray indArray = Nd4j.create(new double[][]{
                inputs
        });
        return indArray;
    }

    @Override
    public Encodable dup() {
        return null;
    }
}
