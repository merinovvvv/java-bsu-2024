package by.merinovvvv.quizer.tasks;

import by.merinovvvv.quizer.Result;
import by.merinovvvv.quizer.Task;
import by.merinovvvv.quizer.tasks.math.AbstractMathTask;
import by.merinovvvv.quizer.tasks.math.MathTask;

public class ExpressionTask extends AbstractMathTask implements MathTask {

    private final int firstArg;
    private final String operator;
    private final int secondArg;

    public ExpressionTask(int num1, int num2, String operator) {
        firstArg = num1;
        secondArg = num2;
        this.operator = operator;
    }

    @Override
    public String getText() {
        return firstArg + operator + secondArg;
    }

    public Result validate(String answer) {
        double correctAnswer = switch (operator) {
            case "+" -> firstArg + secondArg;
            case "-" -> firstArg - secondArg;
            case "*" -> firstArg * secondArg;
            case "/" -> {
                if (secondArg == 0) {
                    throw new ArithmeticException("Division by zero.");
                }
                yield (double) firstArg / secondArg;
            }
            default -> throw new IllegalArgumentException("Invalid operator.");
        };

        try {
            double userAnswer = Double.parseDouble(answer);
            return userAnswer == correctAnswer ? Result.OK : Result.WRONG;
        } catch (NumberFormatException e) {
            return Result.INCORRECT_INPUT;
        }
    }
}
