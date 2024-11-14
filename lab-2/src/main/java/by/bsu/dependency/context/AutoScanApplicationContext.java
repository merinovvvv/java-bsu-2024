package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.function.Function;
import java.util.stream.Collectors;

import static by.bsu.dependency.util.DependencyUtil.decapitalize;

public class AutoScanApplicationContext extends AbstractApplicationContext {
    /**
     * Создает контекст, содержащий классы из пакета {@code packageName}, помеченные аннотацией {@code @Bean}.
     * <br/>
     * Если имя бина в анноации не указано ({@code name} пустой), оно берется из названия класса.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param packageName имя сканируемого пакета
     */
    public AutoScanApplicationContext(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        beanDefinitions = reflections.getTypesAnnotatedWith(Bean.class).stream()
                .collect(Collectors
                .toMap(
                        clazz -> decapitalize(clazz.getSimpleName()),
                        Function.identity()
                )
        );

        beanDefinitions.forEach((beanName, beanClass) -> {
            BeanScope scope = beanClass.getAnnotation(Bean.class).scope();
            beanScopes.put(beanName, scope);
        });
    }
}