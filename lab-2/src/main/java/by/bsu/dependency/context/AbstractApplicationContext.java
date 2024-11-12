package by.bsu.dependency.context;

import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    public boolean isRunning() {
        return !beans.isEmpty();
    }

    @Override
    public boolean containsBean(String name) throws ApplicationContextNotStartedException {
        if (beans.isEmpty()) {
            throw new ApplicationContextNotStartedException();
        }
        return beans.containsKey(name);
    }

    @Override
    public Object getBean(String name) throws ApplicationContextNotStartedException, NoSuchBeanDefinitionException {
        if (beans.isEmpty()) {
            throw new ApplicationContextNotStartedException();
        } else if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("No bean found with name: " + name);
        }
        if (beanScopes.get(name) == BeanScope.PROTOTYPE) {
            Object beanObj = instantiateBean(beanDefinitions.get(name));
            injectDependencies(beanObj);
            return beanObj;
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
                        T beanObj = instantiateBean(clazz);
                        injectDependencies(beanObj);
                        return beanObj;
                    }
                    return clazz.cast(entry.getValue());
                }
            }
            throw new NoSuchBeanDefinitionException("No bean found of type: " + clazz.getName());
        } catch (ClassCastException e) {
            System.out.println("Error while casting a a bean to the clazz: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("No bean found with name: " + name);
        }
        return beanScopes.get(name) == BeanScope.PROTOTYPE;
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("No bean found with name: " + name);
        }
        return beanScopes.get(name) == BeanScope.SINGLETON;
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
                    Object dependency = getBean(field.getType());
                    field.set(beanObj, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
