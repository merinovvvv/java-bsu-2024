package by.merinovvvv.quizer.generators;

import by.merinovvvv.quizer.TaskGenerator;
import by.merinovvvv.quizer.tasks.EquationTask;

class EquationTaskGenerator implements TaskGenerator <EquationTask> {
    /**
     * @param minNumber              минимальное число
     * @param maxNumber              максимальное число
     * @param generateSum            разрешить генерацию с оператором +
     * @param generateDifference     разрешить генерацию с оператором -
     * @param generateMultiplication разрешить генерацию с оператором *
     * @param generateDivision       разрешить генерацию с оператором /
     */

    private final int minNumber;
    private final int maxNumber;
    private final boolean generateSum;
    private final boolean generateDifference;
    private final boolean generateMultiplication;
    private final boolean generateDivision;

    EquationTaskGenerator(
            int minNumber,
            int maxNumber,
            boolean generateSum,
            boolean generateDifference,
            boolean generateMultiplication,
            boolean generateDivision
    ) {
        if (minNumber > maxNumber) {
            throw new IllegalArgumentException("minNumber can't be greater than maxNumber.");
        }
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.generateSum = generateSum;
        this.generateDifference = generateDifference;
        this.generateMultiplication = generateMultiplication;
        this.generateDivision = generateDivision;
    }

    /**
     * return задание типа {@link EquationTask}
     */
    @Override
    public EquationTask generate() {
        int num1 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        int num2 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        int num3 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        String operator = generateSum ? "+" : generateDifference ? "-" : generateMultiplication ? "*" : generateDivision ? "/" : "";
        if (operator.isEmpty()) {
            throw new IllegalArgumentException("No operator was selected.");
        }
        return new EquationTask(num1, num2, num3, operator);
    }
}