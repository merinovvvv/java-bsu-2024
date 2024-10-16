package by.merinovvvv.quizer.tasks;

import by.merinovvvv.quizer.Result;
import by.merinovvvv.quizer.Task;
import by.merinovvvv.quizer.generators.math.MathTaskGenerator;
import by.merinovvvv.quizer.tasks.math.AbstractMathTask;
import by.merinovvvv.quizer.tasks.math.MathTask;

import java.util.Objects;

public class EquationTask extends AbstractMathTask implements MathTask {

    private final int firstArg;
    private final int secondArg;
    private final int equalsArg;
    private final String operator;

    public EquationTask(int num1, int num2, int num3, String operator) {
        firstArg = num1;
        secondArg = num2;
        equalsArg = num3;
        this.operator = operator;
    }

    @Override
    public String getText() {
        return firstArg + "x" + operator + secondArg;
    }

    public Result validate(String answer) {

        if (firstArg == 0) {
            if (!Objects.equals(operator, "*") && !Objects.equals(operator, "/")) {
                if (secondArg == equalsArg) {
                    throw new ArithmeticException("Infinite number of solutions.");
                }
                throw new ArithmeticException("The equation has no solutions.");
            }
            if (equalsArg == 0) {
                throw new ArithmeticException("Infinite number of solutions.");
            }
            throw new ArithmeticException("The equation has no solutions.");
        }

        double correctAnswer = switch (operator) {
            case "+" -> (double) (equalsArg - secondArg) / firstArg;
            case "-" -> (double) (equalsArg + secondArg) / firstArg;
            case "*" -> (double) equalsArg / (firstArg * secondArg);
            case "/" -> {
                if (secondArg == 0) {
                    throw new ArithmeticException("Division by zero.");
                }
                yield (double) equalsArg * secondArg / firstArg;
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
