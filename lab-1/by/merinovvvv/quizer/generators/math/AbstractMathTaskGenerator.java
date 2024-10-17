package by.merinovvvv.quizer.generators.math;

abstract public class AbstractMathTaskGenerator implements MathTaskGenerator {

    @Override
    public Object[] generateMathTask(int maxNumber, int minNumber,
                                        boolean generateSum, boolean generateDifference,
                                        boolean generateMultiplication, boolean generateDivision, boolean monkey) {
        int num1 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        int num2 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        int num3 = (int) (Math.random() * (maxNumber - minNumber + 1) + minNumber);
        String operator = generateSum ? "+" : generateDifference ? "-" : generateMultiplication ? "*" : generateDivision ? "/" : "";
        if (operator.isEmpty()) {
            throw new IllegalArgumentException("No operator was selected.");
        }

        if (operator.equals("*") && ((num1 == 0 || num2 == 0) && num3 != 0) || (operator.equals("/") && num1 == 0 && num3 != 0) || (operator.equals("/") && num2 == 0)) {
            return generateMathTask(maxNumber, minNumber, false, false, generateMultiplication, generateDivision, monkey);
        }

        Object[] arrayToReturn = new Object[4];
        arrayToReturn[0] = num1;
        arrayToReturn[1] = num2;
        arrayToReturn[2] = operator;
        if (monkey) {
            return arrayToReturn;
        }
        arrayToReturn[3] = num3;
        return arrayToReturn;
    }
}
