package com.m2i.model;

import com.m2i.exception.IllegalParameterArgumentException;

import java.math.BigDecimal;

public   abstract class BaseModel extends  Object{


    // Accepte +, chiffres, espaces, tirets, points et parenthèses
    public static final String INVALID_NUMBER_WARNING = "must be a positive value";

    protected void checkAttribute(String parameterName, String parameterValue){
        if(parameterValue == null || parameterValue.isBlank() ){
            throw IllegalParameterArgumentException.forNullOrBlank(parameterName);
        }
    }
    protected void checkAttribute(String parameterName, Integer parameterValue){
        if(parameterValue == null || parameterValue<=0 ){
            throw IllegalParameterArgumentException.forInvalid(parameterName, INVALID_NUMBER_WARNING);
        }
    }
    protected void checkAttribute(String parameterName, BigDecimal parameterValue){
        if(parameterValue == null || parameterValue.compareTo(BigDecimal.ZERO) <= 0 ){
            throw IllegalParameterArgumentException.forInvalid(parameterName, INVALID_NUMBER_WARNING);
        }
    }
    protected void checkAttribute(String parameterName, Object parameterValue){
        if(parameterValue == null  ){
            throw IllegalParameterArgumentException.forNullOrBlank(parameterName);
        }
    }
}
