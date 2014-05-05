package ru.hse.se.shugurov.social_networks;

import java.io.Serializable;

import ru.hse.se.shugurov.Requester;

/**
 * Created by Иван on 05.05.2014.
 */
public abstract class AbstractRequester implements Serializable
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

    public void getTopics(String groupID, RequestResultListener<SocialNetworkTopic> listener)
    {
        throw new UnsupportedOperationException();
    }

    protected SocialNetworkTopic[] getTopics(String topicsJson)
    {
        throw new UnsupportedOperationException();
    }

    public void addTopic(String groupId, String title, String text, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    public void getComments(String groupID, String topicID, RequestResultListener<SocialNetworkEntry> listener)
    {
        throw new UnsupportedOperationException();
    }

    protected SocialNetworkEntry[] getComments(String commentsJson)
    {
        throw new UnsupportedOperationException();
    }


    public void getWallPosts(String groupId, RequestResultListener<SocialNetworkTopic> listener)
    {
        throw new UnsupportedOperationException();
    }

    public void addCommentToTopic(String groupId, String topicId, String text, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    public void getWallComments(String groupId, String postId, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    public void addCommentToWallPost(String groupId, String postId, String text, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    public interface RequestResultListener<T extends SocialNetworkEntry>
    {
        void resultObtained(T[] result);
    }

}
