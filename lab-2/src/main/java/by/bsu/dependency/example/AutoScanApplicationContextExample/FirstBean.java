package by.bsu.dependency.example.AutoScanApplicationContextExample;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "firstBean", scope = BeanScope.PROTOTYPE)
public class FirstBean {

    @PostConstruct
    void init() {
        System.out.println("firstBean PostConstruct works.");
    }

    void printSomething() {
        System.out.println("Hello, I'm first bean");
    }

    void doSomething() {
        System.out.println("First bean is working on a project...");
    }
}
