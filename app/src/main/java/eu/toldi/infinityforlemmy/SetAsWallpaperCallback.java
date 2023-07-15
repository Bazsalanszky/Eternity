package eu.toldi.infinityforlemmy;

public interface SetAsWallpaperCallback {
    void setToHomeScreen(int viewPagerPosition);
    void setToLockScreen(int viewPagerPosition);
    void setToBoth(int viewPagerPosition);
}