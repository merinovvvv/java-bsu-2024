package by.merinovvvv.quizer;

import by.merinovvvv.quizer.tasks.EquationTask;
import by.merinovvvv.quizer.tasks.ExpressionTask;

import java.util.Map;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Map<String, Quiz> quizMap  = getQuizMap();
        Scanner sc = new Scanner(System.in);
        String input = "";

        while (input.isEmpty() || quizMap.get(input) == null) {
            System.out.println("Enter the name of the test...");
            input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input is empty. Try again.");
            }
            Quiz quiz = quizMap.get(input);
            if (quiz == null) {
                System.out.println("Test not found. Try again.");
            }
        }
    }

    /**
     * @return тесты в {@link Map}, где
     * ключ - название теста {@link String}
     * значение - сам тест       {@link Quiz}
     */
    static Map<String, Quiz> getQuizMap() {
        Map<String, Quiz> quizMap = Map.of();
        //quizMap.put("Math", new Quiz(new ExpressionTask(1, 2, "+"), 5));
        return quizMap;
        }
}
