package by.merinovvvv.quizer.generators.math;

import by.merinovvvv.quizer.tasks.EquationTask;

abstract public class AbstractMathTaskGenerator implements MathTaskGenerator {

    @Override
    public Object[] generateMathTask(int maxNumber, int minNumber,
                                        boolean generateSum, boolean generateDifference,
                                        boolean generateMultiplication, boolean generateDivision) {
        int num1 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        int num2 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        int num3 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        String operator = generateSum ? "+" : generateDifference ? "-" : generateMultiplication ? "*" : generateDivision ? "/" : "";
        if (operator.isEmpty()) {
            throw new IllegalArgumentException("No operator was selected.");
        }
        Object[] arrayToReturn = new Object[4];
        arrayToReturn[0] = num1;
        arrayToReturn[1] = num2;
        arrayToReturn[2] = operator;
        arrayToReturn[3] = num3;
        return arrayToReturn;
    }
}
