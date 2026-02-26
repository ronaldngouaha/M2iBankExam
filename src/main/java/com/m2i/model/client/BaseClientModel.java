package com.m2i.model.client;

import com.m2i.exception.IllegalAddressArgumentException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public sealed abstract class BaseModel permits Address, Client {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );
    // Accepte +, chiffres, espaces, tirets, points et parenthèses
    private static final Pattern PHONE_ALLOWED_PATTERN = Pattern.compile("^[+0-9()\\s.-]+$");
    public static final String INVALID_NUMBER_WARNING = "must be a positive value";
    public static final String INVALID_EMAIL_WARNING = "must be a valid email address";
    public static final String INVALID_CHARACTERS_WARNING = "contains invalid characters";
    public static final int MIN_PHONE_DIGITS = 7;
    public static final int MAX_PHONE_DIGITS = 15;
    public static final String INVALID_DIGITS_WARNING = "must contain digits between";

    protected void checkAttribute(String parameterName, String parameterValue){
        if(parameterValue == null || parameterValue.isBlank() ){
            throw IllegalAddressArgumentException.forNullOrBlank(parameterName);
        }
    }
    protected void checkAttribute(String parameterName, Integer parameterValue){
        if(parameterValue == null || parameterValue<=0 ){
            throw IllegalAddressArgumentException.forInvalid(parameterName, INVALID_NUMBER_WARNING);
        }
    }
    protected void checkAttribute(String parameterName, BigDecimal parameterValue){
        if(parameterValue == null || parameterValue.compareTo(BigDecimal.ZERO) <= 0 ){
            throw IllegalAddressArgumentException.forInvalid(parameterName, INVALID_NUMBER_WARNING);
        }
    }
    protected void checkAttribute(String parameterName, Object parameterValue){
        if(parameterValue == null  ){
            throw IllegalAddressArgumentException.forNullOrBlank(parameterName);
        }
    }

    /**
     * Valide qu'une chaîne est une adresse email non vide et bien formée.
     * Réutilise la validation de base (null/blank) puis vérifie le pattern.
     * @param parameterName le nom du paramètre (utilisé dans l'exception)
     * @param email la valeur de l'email à valider
     * @throws IllegalAddressArgumentException si l'email est null, vide ou mal formé
     */
    protected void checkEmailAttribute(String parameterName, String email) {
        // vérifie null/blank via la méthode existante
        checkAttribute(parameterName, email);
        // vérifie le format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw IllegalAddressArgumentException.forInvalid(parameterName, INVALID_EMAIL_WARNING);
        }
    }

    /**
     * Valide un numéro de téléphone : non vide, seulement caractères autorisés
     * et nombre de chiffres raisonnable (entre 7 et 15).
     * @param parameterName nom du paramètre pour l'exception
     * @param phone la valeur du numéro à valider
     */
    protected void checkPhoneAttribute(String parameterName, String phone) {
        // vérifie null/blank
        checkAttribute(parameterName, phone);
        // caractères autorisés
        if (!PHONE_ALLOWED_PATTERN.matcher(phone).matches()) {
            throw IllegalAddressArgumentException.forInvalid(parameterName, INVALID_CHARACTERS_WARNING);
        }
        // compter les chiffres uniquement
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() < MIN_PHONE_DIGITS || digits.length() > MAX_PHONE_DIGITS) {
            throw IllegalAddressArgumentException.forInvalid(parameterName, INVALID_DIGITS_WARNING + " " +MIN_PHONE_DIGITS+" and  "+MAX_PHONE_DIGITS);
        }
    }

}
