package ru.hse.se.shugurov.social_networks;

import java.io.Serializable;

import ru.hse.se.shugurov.utills.Requester;

/**
 * Abstract superclass for requesters of specific social networks assuming tat they use OAuth 2 authentication.
 * Most of the methods are not abstract, however they do not have implementations. Every specific requester implements those methods which make
 * sense for a particular social network. Subclasses have to declare actual code that interacts with
 * social networks, passes data to it and gets a response. Typically it is done with using {@link ru.hse.se.shugurov.utills.Requester}.
 * Subclasses also have to include parsers for a data types they receive from the Internet.
 * There is a need to create a new instance for a every new access token.
 * <p/>
 * Created by Ivan Shugurov
 */
public abstract class AbstractRequester implements Serializable
{
    private AccessToken accessToken;

    /**
     * Creates a new instance with a specified token.
     *
     * @param accessToken
     */
    public AbstractRequester(AccessToken accessToken)
    {
        this.accessToken = accessToken;
    }

    protected AccessToken getAccessToken()
    {
        return accessToken;
    }

    /**
     * Makes requests for obtaining information about topic,s parses web service response and then
     * pushes array of topics to a listener. Passes null if a connection to a server can not be established or
     * response is incorrect
     *
     * @param groupId  id of a group whose posts need to be requested
     * @param listener used to notify caller
     */
    public void getTopics(String groupId, RequestResultListener<SocialNetworkTopic> listener)
    {
    }

    /**
     * Makes a request to create a new topic.If a particular social network does not distinguish a title
     * from a text then it is possible to ignore or use different approach.
     * For example to add an empty line between a title and a text.
     *
     * @param groupId  specifies group which will get a new topic
     * @param title    specifies a title.
     * @param text     text of a topic
     * @param callback used to notify a caller about result of a request
     */
    public void addTopic(String groupId, String title, String text, Requester.RequestResultCallback callback)
    {
    }

    /**
     * Makes requests for obtaining information about comments for a specific topic, parses web service response and then
     * pushes array of comments to a listener. Passes null if a connection to a server can not be established or
     * response is incorrect
     *
     * @param groupID  specifies a group whose comments are desired
     * @param topicID  specifies a topic whose comments are desired
     * @param listener used to notify a caller about result of a request
     */
    public void getComments(String groupID, String topicID, RequestResultListener<SocialNetworkEntry> listener)
    {
    }

    /**
     * Makes requests for obtaining information about wall posts, parses web service response and then
     * pushes array of wall posts to a listener. Passes null if a connection to a server can not be established or
     * response is incorrect
     *
     * @param groupId  specifies a group whose wall posts are desired
     * @param listener used to notify a caller about result of a request
     */
    public void getWallPosts(String groupId, RequestResultListener<SocialNetworkTopic> listener)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Makes a request to add a new comment to the given topic.
     *
     * @param groupId  specifies a group to which a comment will be added
     * @param topicId  specifies a topic to which a comment will be added
     * @param text     text of a comment
     * @param callback used to notify a caller about result of a request
     */
    public void addCommentToTopic(String groupId, String topicId, String text, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @param groupId  specifies a group whose comments are desired
     * @param postId   specifies a wall post whose comments are desired
     * @param callback used to notify a caller about result of a request
     */
    public void getWallComments(String groupId, String postId, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Makes a request to add a new comment to the given wall post.
     *
     * @param groupId  specifies a group to which a comment will be added
     * @param postId   specifies a topic to which a comment will be added
     * @param text     text of a comment
     * @param callback used to notify a caller about result of a request
     */
    public void addCommentToWallPost(String groupId, String postId, String text, Requester.RequestResultCallback callback)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Some social networks can restrict addition of new topics. This method shows if it is possible
     *
     * @return if creation of new topics is allowed
     */
    public boolean canAddPosts()
    {
        return false;
    }

    /**
     * @return if it is good idea to show a number of comments
     */
    public boolean showCommentsQuantity()
    {
        return true;
    }

    /**
     * used to notify a caller about result of a request
     *
     * @param <T> type of elements which will be generated based on information from a web service
     */
    public interface RequestResultListener<T extends SocialNetworkEntry>
    {
        void resultObtained(T[] result);
    }

}
