package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 04.05.2014.
 */
public class FacebookRequester
{
    public static final String REDIRECTION_URL = "https://www.facebook.com/connect/login_success.html";
    public static final String auth = "https://www.facebook.com/dialog/oauth?" +
            "client_id=465339086933483" +
            "&redirect_uri=" + REDIRECTION_URL + "&response_type=token";

}
