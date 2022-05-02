package cn.sdu.oj.domain.bo;


/**
 * 代码语言
 */
public enum LanguageEnum {
    PYTHON2("PYTHON2"),
    PYTHON3("PYTHON3"),
    CPP98("CPP98"),
    CPP11("CPP11"),
    CPP14("CPP14"),
    CPP17("CPP17"),
    CPP20("CPP20"),
    C90("C90"),
    C99("C99"),
    C11("C11"),
    JAVA8("JAVA8"),
    JAVA11("JAVA11");

    LanguageEnum(String language) {
        this.language = language;
    }

    private final String language;

    public String getLanguage() {
        return language;
    }
}
