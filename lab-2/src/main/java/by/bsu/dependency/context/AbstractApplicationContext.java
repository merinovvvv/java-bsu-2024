package by.bsu.dependency.context;

import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import static by.bsu.dependency.util.DependencyUtil.decapitalize;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractApplicationContext implements ApplicationContext {

    Map<String, Class<?>> beanDefinitions;
    final Map<String, Object> beans = new HashMap<>();
    final Map<String, BeanScope> beanScopes = new HashMap<>();

    protected ContextStatus contextStatus = ContextStatus.NOT_STARTED;

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    @Override
    public void start() {
        contextStatus = ContextStatus.STARTED;
        beanDefinitions.forEach((beanName, beanClass) -> {
            if (beanScopes.get(beanName) == BeanScope.SINGLETON) {
                Object beanObj = instantiateBean(beanClass);
                injectDependencies(beanObj);
                beans.put(beanName, beanObj);
                invokePostConstructs(beanClass, beanObj);
            }
        });
    }

    @Override
    public boolean isRunning() {
        return contextStatus == ContextStatus.STARTED;
    }


    @Override
    public boolean containsBean(String name) throws ApplicationContextNotStartedException {
        if (contextStatus == ContextStatus.NOT_STARTED) {
            throw new ApplicationContextNotStartedException();
        }
        return beans.containsKey(name);
    }

    @Override
    public Object getBean(String name) throws ApplicationContextNotStartedException, NoSuchBeanDefinitionException {
        if (contextStatus == ContextStatus.NOT_STARTED) {
            throw new ApplicationContextNotStartedException();
        }
        if (beanScopes.get(name) == BeanScope.PROTOTYPE) {
            prototypeCreateAndInject(name, beanDefinitions.get(name), true);
        } else if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("No bean found with name: " + name);
        }
        return beans.get(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws NoSuchBeanDefinitionException {
        try {
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                if (clazz.isInstance(entry.getValue())) {
                    String beanName = entry.getKey();
                    if (beanScopes.get(beanName) == BeanScope.PROTOTYPE) {
                        return prototypeCreateAndInject(beanName, clazz, true);
                    }
                    return clazz.cast(entry.getValue());
                }
            }
            if (beanScopes.get(decapitalize(clazz.getSimpleName())) == BeanScope.PROTOTYPE) {
                return prototypeCreateAndInject(decapitalize(clazz.getSimpleName()), clazz, false);
            }
            throw new NoSuchBeanDefinitionException("No bean found of type: " + clazz.getName());
        } catch (ClassCastException e) {
            System.out.println("Error while casting a a bean to the clazz: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        if (beanScopes.get(name) != null) {
            return beanScopes.get(name) == BeanScope.PROTOTYPE;
        }
        throw new NoSuchBeanDefinitionException("No bean found with name: " + name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        if (beanScopes.get(name) != null) {
            return beanScopes.get(name) == BeanScope.SINGLETON;
        }
        throw new NoSuchBeanDefinitionException("No bean found with name: " + name);
    }

    <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    void injectDependencies(Object beanObj) {
        for (Field field : beanObj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    if (field.get(beanObj) == null) { // check if the dependency is already injected
                        Object dependency = getBean(field.getType());
                        field.set(beanObj, dependency);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    <T> T prototypeCreateAndInject(String beanName, Class<T> clazz, boolean isPostConstructInvoke) { //test is in the AutoScanApplicationContextTest.java
        T beanObj = instantiateBean(clazz);
        injectDependencies(beanObj);
        beans.put(beanName, beanObj);
        if (isPostConstructInvoke) {
            invokePostConstructs(clazz, beanObj);
        }
        return beanObj;
    }

    void invokePostConstructs(Class<?> beanClass, Object beanObj) { //test is in the AutoScanApplicationContextTest.java
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                try {
                    method.setAccessible(true);
                    method.invoke(beanObj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
