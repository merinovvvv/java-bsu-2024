package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import static by.bsu.dependency.util.DependencyUtil.decapitalize;

public class SimpleApplicationContext extends AbstractApplicationContext {
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
    @Override //TODO move to Abstract
    public void start() {
        contextStatus = ContextStatus.STARTED;
        beanDefinitions.forEach((beanName, beanClass) -> {
            if (beanScopes.get(beanName) == BeanScope.SINGLETON) {
                Object beanObj = instantiateBean(beanClass);
                injectDependencies(beanObj);
                beans.put(beanName, beanObj);
            }
        });
    }
}
