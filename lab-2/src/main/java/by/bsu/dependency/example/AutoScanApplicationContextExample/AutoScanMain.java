package by.bsu.dependency.example.AutoScanApplicationContextExample;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.AutoScanApplicationContext;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

public class AutoScanMain {

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new AutoScanApplicationContext("by.bsu.dependency.example.SimpleApplicationContextExample");
            applicationContext.start();

            by.bsu.dependency.example.AutoScanApplicationContextExample.FirstBean firstBean = (FirstBean) applicationContext.getBean("firstBean"); //TODO ERROR
            by.bsu.dependency.example.AutoScanApplicationContextExample.OtherBean otherBean = (OtherBean) applicationContext.getBean("otherBean");

            firstBean.printSomething();
            firstBean.doSomething();
            otherBean.doSomething();
            otherBean.doSomethingWithFirst();

        } catch (ApplicationContextNotStartedException | NoSuchBeanDefinitionException e) {
            System.out.println(e.getMessage());
        }
    }
}
