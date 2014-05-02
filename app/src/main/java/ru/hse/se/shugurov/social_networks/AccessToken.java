package ru.hse.se.shugurov.social_networks;


import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Created by Иван on 24.03.2014.
 */
public class AccessToken implements Serializable
{
    private final String accessToken;
    private final long expirationTime;

    public AccessToken(String accessToken, long expiresIn)
    {
        this.accessToken = accessToken;
        long currentTime = System.currentTimeMillis();
        this.expirationTime = currentTime + expiresIn * 1000 - 100000;//TODO почему так странно современем?(
    }

    public AccessToken(String serializedToken)
    {
        StringTokenizer tokenizer = new StringTokenizer(serializedToken);
        if (tokenizer.countTokens() != 2)
        {
            throw new IllegalArgumentException("Precondition violated in AccessToken. Incorrect format of a serialized token");
        }
        accessToken = tokenizer.nextToken();
        expirationTime = Long.parseLong(tokenizer.nextToken());
    }

    public long getExpirationTime()
    {
        return expirationTime;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public boolean hasExpired()
    {
        long currentTime = System.currentTimeMillis();
        return currentTime >= expirationTime;
    }

    public String getStringRepresentation()
    {
        return accessToken + " " + expirationTime;
    }

    @Override
    public String toString()
    {
        return accessToken;
    }
}
