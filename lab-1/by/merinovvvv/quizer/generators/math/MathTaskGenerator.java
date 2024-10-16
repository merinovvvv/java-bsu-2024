package by.merinovvvv.quizer.generators.math;

import by.merinovvvv.quizer.TaskGenerator;
import by.merinovvvv.quizer.tasks.math.MathTask;

public interface MathTaskGenerator extends TaskGenerator<MathTask> {
    Object[] generateMathTask(int maxNumber, int minNumber,
                              boolean generateSum, boolean generateDifference,
                              boolean generateMultiplication, boolean generateDivision);
}
