package by.bsu.dependency.context;

import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.example.SimpleApplicationContextExample.FirstBean;
import by.bsu.dependency.example.SimpleApplicationContextExample.OtherBean;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class SimpleApplicationContextTest {

    private ApplicationContext applicationContext;

    static class TestBean {
        public TestBean() {}
    }

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class);
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isFalse();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        applicationContext.start();
        assertThat(applicationContext.isSingleton("firstBean")).isFalse();
        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        applicationContext.start();
        assertThat(applicationContext.isPrototype("firstBean")).isTrue();
        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    @Test
    void testInstantiateBean() {
        AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
        TestBean bean = abstractApplicationContext.instantiateBean(TestBean.class);
        assertNotNull(bean, "The bean should be instantiated");
    }

    @Test
    void testInjectDependencies() throws NoSuchFieldException, IllegalAccessException {
        class TestDependency {}
        class TestBeanInjection {
            @Inject
            private TestDependency dependency;
        }

        TestBeanInjection testBeanInjection = new TestBeanInjection();
        TestDependency testDependency = new TestDependency();

        AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
        abstractApplicationContext.beans.put("testDependency", testDependency);
        abstractApplicationContext.beanScopes.put("testDependency", BeanScope.SINGLETON);

        abstractApplicationContext.injectDependencies(testBeanInjection);
        Field dependencyField = TestBeanInjection.class.getDeclaredField("dependency");
        dependencyField.setAccessible(true);
        assertEquals(testDependency, dependencyField.get(testBeanInjection)); //compare values
    }
}