package eu.toldi.infinityforlemmy.dto;

public class ReadMessageDTO {

    private int person_mention_id;
    private boolean read;
    private String auth;

    public ReadMessageDTO(int person_mention_id, boolean read, String auth) {
        this.person_mention_id = person_mention_id;
        this.read = read;
        this.auth = auth;
    }

    public int getPerson_mention_id() {
        return person_mention_id;
    }

    public boolean isRead() {
        return read;
    }

    public String getAuth() {
        return auth;
    }
}
