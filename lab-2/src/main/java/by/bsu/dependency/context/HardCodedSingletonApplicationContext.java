package by.bsu.dependency.context;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;


public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {

    private final Map<String, Class<?>> beanDefinitions;
    private final Map<String, Object> beans = new HashMap<>();

    /**
     * ! Класс существует только для базового примера !
     * <br/>
     * Создает контекст, содержащий классы, переданные в параметре. Полагается на отсутсвие зависимостей в бинах,
     * а также на наличие аннотации {@code @Bean} на переданных классах.
     * <br/>
     * ! Контекст данного типа не занимается внедрением зависимостей !
     * <br/>
     * ! Создает только бины со скоупом {@code SINGLETON} !
     *
     * @param beanClasses классы, из которых требуется создать бины
     */
    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) {
        this.beanDefinitions = Arrays.stream(beanClasses).collect(
                Collectors.toMap(
                        beanClass -> beanClass.getAnnotation(Bean.class).name(),
                        Function.identity()
                )
        );
    }

    @Override
    public void start() {
        beanDefinitions.forEach((beanName, beanClass) -> beans.put(beanName, instantiateBean(beanClass)));
    }

    @Override
    public boolean isRunning() {
        return !beans.isEmpty();
    }

    /**
     * В этой реализации отсутствуют проверки статуса контекста (запущен ли он).
     */
    @Override
    public boolean containsBean(String name) throws ApplicationContextNotStartedException {
        if (beans.isEmpty()) {
            throw new ApplicationContextNotStartedException();
        }
        return beans.containsKey(name);
    }

    /**
     * В этой реализации отсутствуют проверки статуса контекста (запущен ли он) и исключения в случае отсутствия бина
     */
    @Override
    public Object getBean(String name) throws ApplicationContextNotStartedException, NoSuchBeanDefinitionException {
        if (beans.isEmpty()) {
            throw new ApplicationContextNotStartedException();
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
        return false;
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        if (!beans.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("No bean found with name: " + name);
        }
        return true;
    }

    private <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
