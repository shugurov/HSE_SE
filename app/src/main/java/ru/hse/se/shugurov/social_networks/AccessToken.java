package ru.hse.se.shugurov.social_networks;


import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Represents an access token, provides a possibility to store it is shared preferences as a string.
 * <p/>
 * Created by Ivan Shugurov
 */
public class AccessToken implements Serializable
{
    private final String accessToken;
    private final long expirationTime;

    /**
     * Constructs a new instance of the class
     *
     * @param accessToken actual access token
     * @param expiresIn   time in which given token will expire
     */
    public AccessToken(String accessToken, long expiresIn)
    {
        this.accessToken = accessToken;
        long currentTime = System.currentTimeMillis();
        if (expiresIn == 0)
        {
            expirationTime = 0;
        } else
        {
            this.expirationTime = currentTime + expiresIn * 1000;
        }
    }

    /**
     * Constructs a new object from a string. String has 2 parts separated by a white space. The first part is a token, the second part is expiration time
     *
     * @param serializedToken
     */
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

    /**
     * @return time when a token becomes expired
     */
    public long getExpirationTime()
    {
        return expirationTime;
    }

    /**
     * @return access token
     */
    public String getAccessToken()
    {
        return accessToken;
    }

    /**
     * Checks if a token is expired
     *
     * @return if a token is expired
     */
    public boolean hasExpired()
    {
        if (expirationTime == 0)
        {
            return false;
        } else
        {
            long currentTime = System.currentTimeMillis();
            return currentTime >= expirationTime;
        }
    }

    /**
     * Provides a representation eligible for storing in shared preferences
     *
     * @return
     */
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
