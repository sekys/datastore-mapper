package sk.seky.google.cloud.datastore;

import java.lang.reflect.Method;

/**
 * Created by lsekerak on 18. 10. 2016.
 */
public class Util {
    public static Method getGetter(Class<?> beanClass, String attributeName) throws NoSuchMethodException {
        String name = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
        Method getter;

        try {
            getter = beanClass.getMethod("get" + name);
        } catch (NoSuchMethodException e) {
            getter = beanClass.getMethod("is" + name);
        }
        return getter;
    }

    public static Method getSetter(Class<?> beanClass, String attributeName, Class parameter) throws NoSuchMethodException {
        String name = Character.toUpperCase(attributeName.charAt(0)) + attributeName.substring(1);
        Method setter;

        try {
            setter = beanClass.getMethod("set" + name, parameter);
        } catch (NoSuchMethodException e) {
            throw e;
        }
        return setter;
    }
}
