package by.merinovvvv.quizer;

import java.util.Map;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Map<String, Quiz> quizMap  = getQuizMap();
        System.out.println("Введите название теста...");
        Scanner sc = new Scanner(System.in);
    }

    /**
     * @return тесты в {@link Map}, где
     * ключ - название теста {@link String}
     * значение - сам тест       {@link Quiz}
     */
    static Map<String, Quiz> getQuizMap() {
        Map<String, Quiz> quizMap = Map.of();
        //TODO


        return quizMap;
        }
}
