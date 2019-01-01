package silentflame.database.entities;

import lombok.Getter;

@Getter
public enum Lang {
    RUS("rus"), ENG("eng");

    private String value;

    Lang(String lang) {
        this.value = lang;
    }


    public static Lang fromValue(String name){
        for (Lang lang: Lang.values()) {
            if (lang.value.equals(name))
            {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unkown value="+name);
    }
}
