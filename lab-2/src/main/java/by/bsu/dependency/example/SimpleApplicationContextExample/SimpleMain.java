package by.bsu.dependency.example.SimpleApplicationContextExample;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.SimpleApplicationContext;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

public class SimpleMain {

    public static void main(String[] args) {
        try {

            ApplicationContext applicationContext = new SimpleApplicationContext(
                    FirstBean.class, OtherBean.class
            );
            applicationContext.start();

            FirstBean firstBean = (FirstBean) applicationContext.getBean("firstBean");
            OtherBean otherBean = (OtherBean) applicationContext.getBean("otherBean");

            firstBean.printSomething();
            firstBean.doSomething();
            otherBean.doSomething();
            otherBean.doSomethingWithFirst();
        } catch (ApplicationContextNotStartedException | NoSuchBeanDefinitionException e) {
            System.out.println(e.getMessage());
        }
    }
}
