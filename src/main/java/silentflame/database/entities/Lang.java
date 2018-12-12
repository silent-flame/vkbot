package silentflame.database.entities;

import lombok.Getter;

@Getter
public enum Lang {
    RUS("rus"), ENG("eng");

    private String value;

    Lang(String lang) {
        this.value = lang;
    }

}
