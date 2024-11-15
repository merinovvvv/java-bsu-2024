package by.bsu.dependency.example.AutoScanApplicationContextExample;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "otherBean")
public class OtherBean {

    @Inject
    private FirstBean firstBean;

    @PostConstruct
    void init() {
        System.out.println("otherBean PostConstruct works.");
    }

    void doSomething() {
        System.out.println("Hi, I'm other bean");
    }

    void doSomethingWithFirst() {
        System.out.println("Trying to shake first bean...");
        firstBean.doSomething();
    }
}
