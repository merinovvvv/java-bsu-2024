package by.bsu.dependency.example.HardCodedSingletonApplicationContextExample;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.HardCodedSingletonApplicationContext;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

public class HardCodedMain {

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = new HardCodedSingletonApplicationContext(
                    FirstBean.class, OtherBean.class
            );
            applicationContext.start();

            FirstBean firstBean = (FirstBean) applicationContext.getBean("firstBean");
            OtherBean otherBean = (OtherBean) applicationContext.getBean("otherBean");

            firstBean.doSomething();
            otherBean.doSomething();

            // Метод падает, так как в классе HardCodedSingletonApplicationContext не реализовано внедрение зависимостей
            // otherBean.doSomethingWithFirst();
        } catch (ApplicationContextNotStartedException | NoSuchBeanDefinitionException e) {
            System.out.println(e.getMessage());
        }
    }
}
