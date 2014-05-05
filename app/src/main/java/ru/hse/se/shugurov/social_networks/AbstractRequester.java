package ru.hse.se.shugurov.social_networks;

import ru.hse.se.shugurov.Requester;

/**
 * Created by Иван on 05.05.2014.
 */
public abstract class AbstractRequester
{
    private AccessToken accessToken;

    public AbstractRequester(AccessToken accessToken)
    {
        this.accessToken = accessToken;
    }

    protected AccessToken getAccessToken()
    {
        return accessToken;
    }

    public void getTopics(String groupID, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    public SocialNetworkTopic[] getTopics(String topicsJson)
    {
        throw new UnsupportedOperationException();
    }

    public void addTopic(String groupId, String title, String text, Requester.RequestResultCallback callback)//TODO что за бред в facebook?
    {
        throw new UnsupportedOperationException();
    }

    public void getComments(String groupID, String topicID, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    public SocialNetworkEntry[] getComments(String commentsJson)
    {
        throw new UnsupportedOperationException();
    }

    public interface RequestResult<T extends SocialNetworkEntry>//TODO
    {
        void obtainResult(T[] result);
    }

}
