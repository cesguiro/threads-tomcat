package es.cesguiro.threads_tomcat;

public class ThreadLocalUser {

    private static final ThreadLocal<String> USER_BY_THREAD = new ThreadLocal<>();

    public static void setUser(String user) {
        USER_BY_THREAD.set(user);
    }

    public static String getUser() {
        return USER_BY_THREAD.get();
    }

    public static void clearUser() {
        USER_BY_THREAD.remove();
    }
}
