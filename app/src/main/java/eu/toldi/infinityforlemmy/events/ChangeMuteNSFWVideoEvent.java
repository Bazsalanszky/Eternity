package eu.toldi.infinityforlemmy.events;

public class ChangeMuteNSFWVideoEvent {
    public boolean muteNSFWVideo;

    public ChangeMuteNSFWVideoEvent(boolean muteNSFWVideo) {
        this.muteNSFWVideo = muteNSFWVideo;
    }
}
