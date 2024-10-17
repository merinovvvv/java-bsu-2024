package by.merinovvvv.quizer;

import by.merinovvvv.quizer.generators.EquationTaskGenerator;
import by.merinovvvv.quizer.generators.ExpressionTaskGenerator;
import by.merinovvvv.quizer.tasks.math.MathTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Map<String, Quiz> quizMap  = getQuizMap();
        Scanner sc = new Scanner(System.in);
        String input = "";
        Quiz quiz = null;

        while (input.isEmpty() || quiz == null) {
            System.out.println("Enter the name of the test...");
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input is empty. Try again.");
            }
            quiz = quizMap.get(input);
            if (quiz == null) {
                System.out.println("Test not found. Try again.");
            }
        }

        while(!quiz.isFinished()) {
            System.out.println(quiz.nextTask().getText());
            System.out.println("Input the answer: ");
            String answer = sc.nextLine().trim();
            Result res = quiz.provideAnswer(answer);
            if (res == Result.OK) {
                System.out.println("Correct!");
            } else if (res == Result.WRONG) {
                System.out.println("Wrong!");
            } else {
                System.out.println("Incorrect input!");
            }
        }
        System.out.println("Test is finished. The results are:");
        System.out.println(quiz.getCorrectAnswerNumber() + " correct answers;");
        System.out.println(quiz.getWrongAnswerNumber() + " wrong answers;");
        System.out.println(quiz.getIncorrectInputNumber() + " incorrect inputs.");
        System.out.println("Your final mark is - " + quiz.getMark());
    }

    /**
     * @return тесты в {@link Map}, где
     * ключ - название теста {@link String}
     * значение - сам тест       {@link Quiz}
     */
    static Map<String, Quiz> getQuizMap() {

        Map<String, Quiz> quizMap = new HashMap<>(Map.of());

        TaskGenerator<MathTask> expressionTaskGeneratorSum = new ExpressionTaskGenerator(1, 100, true, false, false, false);
        TaskGenerator<MathTask> equationTaskGeneratorSum = new EquationTaskGenerator(1, 28, true, false, false, false);
        quizMap.put("expression sum", new Quiz(expressionTaskGeneratorSum, 10));
        quizMap.put("equation sum", new Quiz(equationTaskGeneratorSum, 10));

        TaskGenerator<MathTask> expressionTaskGeneratorDiff = new ExpressionTaskGenerator(1, 100, false, true, false, false);
        TaskGenerator<MathTask> equationTaskGeneratorDiff = new EquationTaskGenerator(1, 28, false, true, false, false);
        quizMap.put("expression difference", new Quiz(expressionTaskGeneratorDiff, 10));
        quizMap.put("equation difference", new Quiz(equationTaskGeneratorDiff, 10));

        TaskGenerator<MathTask> expressionTaskGeneratorMulti = new ExpressionTaskGenerator(1, 100, false, false, true, false);
        TaskGenerator<MathTask> equationTaskGeneratorMulti = new EquationTaskGenerator(1, 28, false, false, true, false);
        quizMap.put("expression multiplication", new Quiz(expressionTaskGeneratorMulti, 10));
        quizMap.put("equation multiplication", new Quiz(equationTaskGeneratorMulti, 10));

        TaskGenerator<MathTask> expressionTaskGeneratorDiv = new ExpressionTaskGenerator(1, 100, false, false, false, true);
        TaskGenerator<MathTask> equationTaskGeneratorDiv = new EquationTaskGenerator(1, 28, false, false, false, true);
        quizMap.put("expression division", new Quiz(expressionTaskGeneratorDiv, 10));
        quizMap.put("equation division", new Quiz(equationTaskGeneratorDiv, 10));
        return quizMap;
    }
}
