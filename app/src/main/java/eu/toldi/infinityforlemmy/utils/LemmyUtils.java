package eu.toldi.infinityforlemmy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LemmyUtils {
    public static String actorID2FullName(String url) {
        String[] splitURL = url.split("/");
        String userName = splitURL[splitURL.length - 1];
        String domain = splitURL[2];
        return (userName.contains("@")) ? userName : userName + "@" + domain;
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

    public static Long dateStringToMills(String dateStr) {
        long postTimeMillis = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            postTimeMillis = ZonedDateTime.parse(dateStr,
                    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Z"))).toInstant().toEpochMilli();
        } else {
            dateStr = dateStr.substring(0, dateStr.lastIndexOf(".") + 4) + 'Z';
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    postTimeMillis = date.getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return postTimeMillis;
    }
}
