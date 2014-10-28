/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package cf.spring.servicebroker;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Bunch of utility methods for accessors.
 *
 * @author Sebastien Gerard
 */
public final class AccessorUtils {

    private AccessorUtils() {
    }

    /**
     * Finds a method annotated with the specified annotation. This method can
     * be defined in the specified class, or one of its parents.
     *
     * @return the matching method, or <tt>null</tt> if any
     */
    public static <T extends Annotation> Method findMethodWithAnnotation(Class<?> clazz, Class<T> annotationType) {
        Method annotatedMethod = null;
        for (Method method : clazz.getDeclaredMethods()) {
            T annotation = AnnotationUtils.findAnnotation(method, annotationType);
            if (annotation != null) {
                if (annotatedMethod != null) {
                    throw new BeanCreationException("Only ONE method with @" + annotationType.getName()
                          + " is allowed on " + clazz.getName() + ".");
                }
                annotatedMethod = method;
            }
        }

        if((annotatedMethod != null) || clazz.equals(Object.class)){
            return annotatedMethod;
        } else {
            return findMethodWithAnnotation(clazz.getSuperclass(), annotationType);
        }
    }

    /**
     * Invokes the method of the specified object with some method arguments.
     */
    public static Object invokeMethod(Object object, Method method, Object... args) throws Throwable {
        try {
            return method.invoke(object, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * Validates that the method annotated with the specified annotation returns
     * the expected type.
     */
    public static void validateReturnType(Method method, Class<? extends Annotation> annotationType,
                                          Class<?> expectedReturnType) {
        if (!method.getReturnType().equals(expectedReturnType)) {
            throw new BeanCreationException("Method " + method.getName() + " annotated with @"
                  + annotationType.getName() + " must have a return type of "
                  + expectedReturnType.getName());
        }
    }

    /**
     * Validates that the method annotated with the specified annotation has a single
     * argument of the expected type.
     */
    public static void validateArgument(Method method, Class<? extends Annotation> annotationType,
                                        Class<?> expectedParameterType) {
        if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(expectedParameterType)) {
            throw new BeanCreationException(String.format(
                  "Method %s with @%s MUST take a single argument of type %s",
                  method,
                  annotationType.getName(),
                  expectedParameterType.getName()
            ));
        }
    }
}
