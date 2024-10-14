package by.merinovvvv.quizer.generators;

import by.merinovvvv.quizer.Task;
import by.merinovvvv.quizer.TaskGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PoolTaskGenerator implements TaskGenerator<Task> {

    private final boolean allowDuplicate;
    private final List<Task> tasks;
    private final List<Task> usedTasks;
    private final Random random;

    /**
     * Конструктор с переменным числом аргументов
     *
     * @param allowDuplicate разрешить повторения
     * @param tasks          задания, которые в конструктор передаются через запятую
     */
    PoolTaskGenerator(
            boolean allowDuplicate,
            Task... tasks
    ) {
        this.allowDuplicate = allowDuplicate;
        this.tasks = new ArrayList<>(List.of(tasks));
        usedTasks = new ArrayList<>();
        random = new Random();
    }

    /**
     * Конструктор, который принимает коллекцию заданий
     *
     * @param allowDuplicate разрешить повторения
     * @param tasks          задания, которые передаются в конструктор в Collection (например, {@link LinkedList})
     */
    PoolTaskGenerator(
            boolean allowDuplicate,
            Collection<Task> tasks
    ) {
        this.allowDuplicate = allowDuplicate;
        this.tasks = new ArrayList<>(tasks);
        usedTasks = new ArrayList<>();
        random = new Random();
    }

    /**
     * @return случайная задача из списка
     */
    public Task generate() {

        if (tasks.isEmpty()) {
            throw new RuntimeException("No tasks available to generate");
        }

        if (!allowDuplicate && usedTasks.size() == tasks.size()) {
            throw new RuntimeException("No tasks available to generate");
        }

        Task task;
        do {
            int index = random.nextInt(tasks.size());
            task = tasks.get(index);
        } while (!allowDuplicate && usedTasks.contains(task));

        if (!allowDuplicate) {
            usedTasks.add(task);
        }

        return task;
    }
}