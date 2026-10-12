package es.cesguiro.threads_tomcat;

public class SharedUser {

    private static volatile String user;

    public static void setUser(String newUser) {
        user = newUser;
    }

    public static String getUser() {
        return user;
    }
}
