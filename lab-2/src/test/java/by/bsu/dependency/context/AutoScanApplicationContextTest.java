package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.AutoScanApplicationContextExample.FirstBean;
import by.bsu.dependency.example.AutoScanApplicationContextExample.OtherBean;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class AutoScanApplicationContextTest {

    private ApplicationContext applicationContext;

    static class TestBean {
        public TestBean() {}
    }

    @BeforeEach
    void init() {
        applicationContext = new AutoScanApplicationContext("by.bsu.dependency.example.AutoScanApplicationContextExample");
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

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
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
        assertNotNull(bean);
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

    @Test
    void testAutoPackageScan() {
        Reflections reflections = new Reflections("by.bsu.dependency.example.AutoScanApplicationContextExample", Scanners.TypesAnnotated);
        Set<Class<?>> beanClasses = reflections.getTypesAnnotatedWith(Bean.class);
        assertNotNull(beanClasses);
        Set<Class<?>> beanClassesHardCoded = new HashSet<>();

        beanClassesHardCoded.add(FirstBean.class);
        beanClassesHardCoded.add(OtherBean.class);

        assertEquals(beanClasses, beanClassesHardCoded);
    }

    @Test
    void testBeanAnnotationBeans() {
        AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
        for (Map.Entry<String, Class<?>> entry : abstractApplicationContext.beanDefinitions.entrySet()) {
            assertTrue(entry.getValue().isAnnotationPresent(Bean.class));
            assertEquals(1, entry.getValue().getAnnotations().length);
        }
    }

    @Test
    void testInvokePostConstructs() {
        class TestDependency {}

        class TestBeanWithPostConstruct {
            @Inject
            private TestDependency dependency;
            boolean postConstructCalled = false;

            @PostConstruct
            void init() {
                assertNotNull(dependency);
                postConstructCalled = true;
            }
        }

        TestBeanWithPostConstruct testBeanWithPostConstruct = new TestBeanWithPostConstruct();
        TestDependency testDependency = new TestDependency();

        AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
        abstractApplicationContext.beans.put("testDependency", testDependency);
        abstractApplicationContext.beanScopes.put("testDependency", BeanScope.SINGLETON);
        abstractApplicationContext.injectDependencies(testBeanWithPostConstruct);
        abstractApplicationContext.invokePostConstructs(TestBeanWithPostConstruct.class, testBeanWithPostConstruct);

        assertTrue(testBeanWithPostConstruct.postConstructCalled);
    }

    @Test
    void testPrototypeCreateAndInject() {
        applicationContext.start();

        AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
        TestBean testBean = abstractApplicationContext.prototypeCreateAndInject("testBean", TestBean.class, true);

        assertNotNull(testBean);
        assertTrue(abstractApplicationContext.beans.containsKey("testBean"));
        assertEquals(testBean, abstractApplicationContext.beans.get("testBean"));
        assertTrue(abstractApplicationContext.containsBean("testBean"));
    }

    @Test
    void instantiateBeanTest() {
        AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) applicationContext;
        assertNotNull(abstractApplicationContext.instantiateBean(AutoScanApplicationContextTest.TestBean.class));
    }
}