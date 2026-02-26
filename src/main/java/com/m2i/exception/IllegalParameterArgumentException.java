package com.m2i.exception;

public class IllegalParameterArgumentException extends IllegalArgumentException{

    private static final String NULL_MESSAGE = "The parameter %s cannot be null or blank";
    private static final String INVALID_MESSAGE = "Invalid value for parameter %s: %s";


    // constructeur par défaut (null/blanc)
    public IllegalParameterArgumentException(String parameterName){
        super(String.format(NULL_MESSAGE, parameterName));
    }

    // constructeur avec raison détaillée
    public IllegalParameterArgumentException(String parameterName, String reason){
        super(String.format(INVALID_MESSAGE, parameterName, reason));
    }

    // usines statiques pour faciliter la création depuis le code appelant
    public static IllegalParameterArgumentException forNullOrBlank(String parameterName){
        return new IllegalParameterArgumentException(parameterName);
    }

    public static IllegalParameterArgumentException forInvalid(String parameterName, String reason){
        return new IllegalParameterArgumentException(parameterName, reason);
    }
}
