package org.example.context;

public class BaseContext {

    public static ThreadLocal<String> currentLocal = new ThreadLocal<>();

    public static String getCurrentId() { return currentLocal.get(); }

    public static void setCurrentId(String id) { currentLocal.set(id); }

    public static void removeCurrentId() { currentLocal.remove(); }

}
