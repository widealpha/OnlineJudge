package cn.sdu.judge.exceptions;

import cn.sdu.judge.bean.LanguageEnum;

public class CurrentNotSupportException extends Exception {
    public CurrentNotSupportException(LanguageEnum languageEnum) {
        super(languageEnum.name() + " is not supported");
    }

    public CurrentNotSupportException(String language) {
        super(language + " is not supported");
    }
}
