package ch.jalu.configme;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.bytecode.AccessFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class NullabilityAnnotationConsistencyTest {

    public static void main(String... args) throws Exception {
        Reflections reflections = new Reflections("ch.jalu.configme",
            Scanners.SubTypes.filterResultsBy(c -> true));

        Set<String> packageBlacklist = excludedPackages();

        List<Class<?>> classes = reflections.getSubTypesOf(Object.class)
            .stream()
            .filter(clz -> !isTestClassOrWithin(clz))
            .filter(clz -> packageBlacklist.stream().noneMatch(bl -> clz.getName().startsWith(bl)))
            .collect(Collectors.toList());

        ClassPool pool = ClassPool.getDefault();

        Map<Class<?>, List<CtMethod>> declaredMethodsByClass = new HashMap<>();
        Map<Class<?>, List<CtConstructor>> declaredConstructorsByClass = new HashMap<>();


        for (Class<?> clazz : classes) {
            CtClass result = pool.get(clazz.getName());
            declaredMethodsByClass.put(clazz, Arrays.asList(result.getDeclaredMethods()));
            declaredConstructorsByClass.put(clazz, Arrays.asList(result.getDeclaredConstructors()));
        }

        List<String> errors = new ArrayList<>();
        for (Map.Entry<Class<?>, List<CtMethod>> classAndMethods : declaredMethodsByClass.entrySet()) {

            for (CtMethod method : classAndMethods.getValue()) {
                if ((method.getMethodInfo().getAccessFlags() & AccessFlag.SYNTHETIC) != 0) {
                    continue;
                }


                List<String> errorsForMethod = new ArrayList<>();
                if (!method.hasAnnotation(NotNull.class)
                        && !method.hasAnnotation(Nullable.class)
                        && !method.getReturnType().isPrimitive()) {
                    errorsForMethod.add("missing annotation on return value");
                }

                Object[][] annotations = method.getParameterAnnotations();
                CtClass[] parameterTypes = method.getParameterTypes();
                errorsForMethod.addAll(findErrorsForParams(annotations, parameterTypes));

                if (!errorsForMethod.isEmpty()) {
                    String methodString = method.getDeclaringClass().getName() +"#" + method.getName() + "("
                        + Arrays.stream(method.getParameterTypes()).map(CtClass::getSimpleName).collect(Collectors.joining(", "))
                        + ")";
                    errors.addAll(errorsForMethod.stream()
                        .map(err -> methodString + ": " + err)
                        .collect(Collectors.toList()));

                }
            }
        }

        for (Map.Entry<Class<?>, List<CtConstructor>> constructorsByClass : declaredConstructorsByClass.entrySet()) {
            for (CtConstructor ctConstructor : constructorsByClass.getValue()) {
                Object[][] annotations = ctConstructor.getParameterAnnotations();
                CtClass[] parameterTypes = ctConstructor.getParameterTypes();
                List<String> errorsForConstructor = findErrorsForParams(annotations, parameterTypes);
                if (!errorsForConstructor.isEmpty()) {
                    String constructorString = "Constructor " + ctConstructor.getLongName();
                    errors.addAll(errorsForConstructor.stream()
                        .map(err -> constructorString + ": " + err)
                        .collect(Collectors.toList()));
                }
            }
        }

        System.out.println(errors.size() + " errors");
        System.out.println(String.join("\n- ", errors));
    }

    private static List<String> findErrorsForParams(Object[][] annotations, CtClass[] parameterTypes) {
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < parameterTypes.length; i++) {
            CtClass parameterType = parameterTypes[i];
            boolean hasNullabilityAnnotation = Arrays.stream(annotations[i])
                .map(anno -> ((Annotation) anno).annotationType())
                .anyMatch(annoType -> annoType == NotNull.class || annoType == Nullable.class);

            if (parameterType.isPrimitive() && hasNullabilityAnnotation) {
                errors.add("param " + i + " is primitive but has a nullability annotation");
            } else if (!parameterType.isPrimitive() && !hasNullabilityAnnotation) {
                errors.add("param " + i + " does not have a nullability annotation");
            }
        }
        return errors;
    }

    private static Set<String> excludedPackages() {
        Set<String> blacklist = new HashSet<>();
        blacklist.add("ch.jalu.configme.beanmapper.command.");
        blacklist.add("ch.jalu.configme.beanmapper.typeissues.");
        blacklist.add("ch.jalu.configme.beanmapper.worldgroup.");
        blacklist.add("ch.jalu.configme.demo.");
        blacklist.add("ch.jalu.configme.samples.");
        return blacklist;
    }

    private static boolean isTestClassOrWithin(Class<?> clazz) {
        return clazz.getName().endsWith("Test")
            || clazz.getEnclosingClass() != null && clazz.getEnclosingClass().getName().endsWith("Test");
    }
}
