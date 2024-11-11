package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static by.bsu.dependency.util.DependencyUtil.decapitalize;

public class SimpleApplicationContext extends AbstractApplicationContext {

    private final Map<String, Class<?>> beanDefinitions;
    private final Map<String, Object> beans = new HashMap<>();
    private final Map<String, BeanScope> beanScopes = new HashMap<>();


    /**
     * Создает контекст, содержащий классы, переданные в параметре.
     * <br/>
     * Если на классе нет аннотации {@code @Bean}, имя бина получается из названия класса, скоуп бина по дефолту
     * считается {@code Singleton}.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public SimpleApplicationContext(Class<?>... beanClasses) {
        this.beanDefinitions = Arrays.stream(beanClasses).collect(
                Collectors.toMap(
                        beanClass -> {
                            if (!beanClass.isAnnotationPresent(Bean.class)) {
                                return decapitalize(beanClass.getName());
                            } else {
                                return beanClass.getAnnotation(Bean.class).name();
                            }
                        },
                        Function.identity()
                )
        );

        this.beanDefinitions.forEach((beanName, beanClass) -> {
            BeanScope scope = beanClass.isAnnotationPresent(Bean.class) ?
                    beanClass.getAnnotation(Bean.class).scope() : BeanScope.SINGLETON;
            beanScopes.put(beanName, scope);
        });
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
    @Override
    public void start() {
        beanDefinitions.forEach((beanName, beanClass) -> {
            if (beanScopes.get(beanName) == BeanScope.SINGLETON) {
                Object bean = instantiateBean(beanClass);
                injectDependencies(bean);
                beans.put(beanName, bean);
            }
        });
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
            Object bean = instantiateBean(beanDefinitions.get(name));
            injectDependencies(bean);
            return bean;
        }
        return beans.get(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws NoSuchBeanDefinitionException {
        try {
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                if (clazz.isInstance(entry.getValue())) {
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

    private <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void injectDependencies(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                try {
                    Object dependency = getBean(field.getType());
                    field.set(bean, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
