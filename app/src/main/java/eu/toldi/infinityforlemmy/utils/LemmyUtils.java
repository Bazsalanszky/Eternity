package eu.toldi.infinityforlemmy.utils;

public class LemmyUtils {
    public static String actorID2FullName(String url) {
        String[] splitURL = url.split("/");
        String userName = splitURL[splitURL.length - 1];
        String domain = splitURL[2];
        return userName + "@" + domain;
    }

    public static String qualifiedCommunityName2ActorId(String qualifiedName) {
        String[] splitQualifiedName = qualifiedName.split("@");
        String userName = splitQualifiedName[0];
        String domain = splitQualifiedName[1];
        return "https://" + domain + "/c/" + userName;
    }

    public static String qualifiedUserName2ActorId(String qualifiedName) {
        String[] splitQualifiedName = qualifiedName.split("@");
        String userName = splitQualifiedName[0];
        String domain = splitQualifiedName[1];
        return "https://" + domain + "/u/" + userName;
    }
}
