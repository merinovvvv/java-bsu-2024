package by.merinovvvv.quizer;

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
        TaskGenerator<MathTask> taskGenerator = new ExpressionTaskGenerator(1, 6, true, false, false, false);
        Map<String, Quiz> quizMap = new HashMap<>(Map.of());
        quizMap.put("test1", new Quiz(taskGenerator, 6));
        return quizMap;
    }
}
