package eu.toldi.infinityforlemmy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SortType {

    @NonNull
    private final Type type;
    @Nullable
    private final Time time;

    public SortType(@NonNull Type type) {
        this(type, null);
    }

    public SortType(@NonNull Type type, @Nullable Time time) {
        this.type = type;
        this.time = time;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    @Nullable
    public Time getTime() {
        return time;
    }

    public enum Type {
        ACTIVE("Active", "Active"),
        HOT("Hot", "Hot"),
        NEW("New", "New"),
        OLD("Old", "Old"),
        TOP("Top", "Top"),
        MOST_COMMENTS("MostCommentes", "Most Commentes"),
        NEW_COMMENTS("NewCommentes", "New Commentes"),

        TOP_HOUR("TopHour", "Top"),
        TOP_SIX_HOURS("TopSixHour", "Top"),
        TOP_TWELVE_HOURS("TopTwelveHour", "Top"),
        TOP_DAY("TopDay", "Top"),
        TOP_WEEK("TopWeek", "Top"),
        TOP_MONTH("month", "Top"),
        TOP_THREE_MONTHS("TopThreeMonths", "Top"),
        TOP_SIX_MONTHS("TopSixMonths", "Top"),
        TOP_NINE_MONTHS("TopNineMonths", "Top"),
        TOP_YEAR("TopYear", "Top"),
        TOP_ALL("TopAll", "Top");

        public final String value;
        public final String fullName;

        Type(String value, String fullName) {
            this.value = value;
            this.fullName = fullName;
        }
    }

    public enum Time {
        HOUR("TopHour", "Top Hour"),
        SIX_HOURS("TopSixHour", "Top Six Hours"),
        TWELVE_HOURS("TopTwelveHour", "Top Twelve Hours"),
        DAY("day", "Day"),
        WEEK("week", "Week"),
        MONTH("month", "Month"),
        THREE_MONTHS("TopThreeMonth", "Top Three Months"),
        SIX_MONTHS("TopSixMonth", "Top Six Months"),
        NINE_MONTHS("TopNineMonth", "Top Nine Months"),
        YEAR("year", "Year"),
        ALL("all", "All Time");

        public final String value;
        public final String fullName;

        Time(String value, String fullName) {
            this.value = value;
            this.fullName = fullName;
        }
    }
}
