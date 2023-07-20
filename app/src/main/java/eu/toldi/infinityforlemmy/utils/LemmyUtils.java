package eu.toldi.infinityforlemmy.utils;

public class LemmyUtils {
    public static String actorID2FullName(String url) {
        String[] splitURL = url.split("/");
        String userName = splitURL[splitURL.length - 1];
        String domain = splitURL[2];
        return userName + "@" + domain;
    }
}
