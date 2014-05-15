package ru.hse.shugurov.gui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Arrays;

import ru.hse.shugurov.R;
import ru.hse.shugurov.social_networks.AbstractRequester;
import ru.hse.shugurov.social_networks.CommentsAdapter;
import ru.hse.shugurov.social_networks.SocialNetworkEntry;
import ru.hse.shugurov.social_networks.SocialNetworkProfile;
import ru.hse.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.shugurov.social_networks.StateListener;
import ru.hse.shugurov.utills.ImageLoader;
import ru.hse.shugurov.utills.ImageViewProxy;
import ru.hse.shugurov.utills.Requester;

/**
 * Mostly used to show vk wall post comments
 * <p/>
 * Fragment requires following arguments:
 * <ul>
 * <li>{@link ru.hse.shugurov.social_networks.SocialNetworkTopic} with a key specified by {@code WALL_COMMENTS_POST_TAG}. Passed as parcelable object</li>
 * <li>{@link ru.hse.shugurov.social_networks.StateListener} with a key specified by {@code COMMENTS_LISTENER_TAG}. Passed as serializable object.</li>
 * </ul>
 * <p/>
 * For other required arguments see{@link ru.hse.shugurov.gui.SocialNetworkAbstractList}
 * <p/>
 * Created by Ivan Shugurov
 */
public class WallCommentsFragment extends SocialNetworkAbstractList
{
    /*constants used as keys in bundle object*/
    public static final String WALL_COMMENTS_TAG = "wall_comments";
    public static final String WALL_COMMENTS_POST_TAG = "wall_comments_post";
    public final static String COMMENTS_LISTENER_TAG = "comments_listener_tag";
    private final static String TYPED_COMMENT = "wall_typed_comment";


    private SocialNetworkTopic post;
    private SocialNetworkEntry[] comments;
    private int containerWidth;
    private String commentText;
    private EditText input;
    private View headerView;
    private View footerView;
    private StateListener stateListener;

    @Override
    public void setArguments(Bundle args)
    {
        super.setArguments(args);
        if (args != null)
        {
            post = args.getParcelable(WALL_COMMENTS_POST_TAG);
            stateListener = (StateListener) args.getSerializable(COMMENTS_LISTENER_TAG);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            post = savedInstanceState.getParcelable(WALL_COMMENTS_POST_TAG);
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(WALL_COMMENTS_TAG);
            if (parcelables == null)
            {
                comments = new SocialNetworkEntry[0];
            } else
            {
                comments = Arrays.copyOf(parcelables, parcelables.length, SocialNetworkEntry[].class);
            }
            commentText = savedInstanceState.getString(TYPED_COMMENT);
            stateListener = (StateListener) savedInstanceState.getSerializable(COMMENTS_LISTENER_TAG);
        }
    }

    @TargetApi(13)
    private int getScreenSizeAfterAPI13(Display display)
    {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @SuppressWarnings("deprecation")
    private int getScreenSizeBeforeAPI13(Display display)
    {
        return display.getWidth();
    }

    /*determines api level and calls corresponding method to get screen width*/
    private int getScreenWidth()
    {
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13)
        {
            return getScreenSizeAfterAPI13(display);
        } else
        {
            return getScreenSizeBeforeAPI13(display);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        containerWidth = container.getWidth() - container.getPaddingLeft() - container.getPaddingRight();
        if (containerWidth == 0)
        {
            containerWidth = getScreenWidth() - container.getPaddingLeft() - container.getPaddingRight();
        }
        return resultView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (comments == null)
        {
            loadComments();
        } else
        {
            boolean emptyProfilesOccur = false;
            for (int i = 0; i < comments.length; i++)
            {
                if (comments[i].getAuthor().getFullName() == null)
                {
                    emptyProfilesOccur = true;
                    break;
                }
            }
            if (emptyProfilesOccur)
            {
                loadComments();
            } else
            {
                instantiateAdapter();
            }
        }
        getListView().setSelector(new StateListDrawable());
    }

    /*requests a list of comments via requester*/
    private void loadComments()
    {
        getRequester().getWallComments(getGroupId(), post.getId(), new AbstractRequester.RequestResultListener<SocialNetworkEntry>()
        {
            @Override
            public void resultObtained(SocialNetworkEntry[] resultComments)
            {
                if (resultComments == null)
                {
                    Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                } else
                {
                    comments = resultComments;
                    instantiateAdapter();
                }
            }
        });
    }

    /*creates header view, footer view and sets adapter to a list viw*/
    private void instantiateAdapter()
    {
        try
        {
            CommentsAdapter responsesAdapter = new CommentsAdapter(getActivity(), comments);
            if (headerView == null)
            {
                createHeaderView();
                getListView().addHeaderView(headerView);
            }
            if (footerView == null)
            {
                createFooterView();
                getListView().addFooterView(footerView);
            }
            setListAdapter(responsesAdapter);
            setListShown(true);
        } catch (Exception e)
        {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(WALL_COMMENTS_TAG, comments);
        outState.putParcelable(WALL_COMMENTS_POST_TAG, post);
        outState.putString(TYPED_COMMENT, input.getText().toString());
        outState.putSerializable(COMMENTS_LISTENER_TAG, stateListener);
    }


    /*creates header view and stores as class field. Header view demonstrates wall post*/
    private void createHeaderView()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        headerView = inflater.inflate(R.layout.vk_wall_post, null, false);
        ImageLoader imageLoader = ImageLoader.instance();
        SocialNetworkProfile author = post.getAuthor();
        ImageView authorPhoto = (ImageView) headerView.findViewById(R.id.vk_post_author_photo);
        authorPhoto.setImageBitmap(null);
        float weightSum = ((LinearLayout) headerView).getWeightSum();
        int photoSize = (int) (containerWidth * (1 / weightSum));
        ImageViewProxy authorPhotoProxy = new ImageViewProxy(authorPhoto, photoSize);
        imageLoader.displayImage(author.getPhoto(), authorPhotoProxy);
        ImageView attachedPicture = (ImageView) headerView.findViewById(R.id.vk_wall_attached_picture);
        attachedPicture.setImageBitmap(null);
        if (post.getAttachedPicture() != null)
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            photoSize = containerWidth - photoSize;
            attachedPicture.getLayoutParams().height = photoSize;
            imageLoader.displayImage(post.getAttachedPicture(), attachedPicture);
        } else
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        ((TextView) headerView.findViewById(R.id.vk_wall_post_author_name)).setText(author.getFullName());
        ((TextView) headerView.findViewById(R.id.vk_wall_post_text)).setText(Html.fromHtml(post.getText()));
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) headerView.findViewById(R.id.footer_date)).setText(format.format(post.getDate()));
    }

    /*creates footer view and stores as class field. Footer view shows a form for adding comments.*/
    private void createFooterView()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        footerView = inflater.inflate(R.layout.send_form, null, false);
        input = (EditText) footerView.findViewById(R.id.send_form_text);
        input.setText(commentText);
        Button sendButton = (Button) footerView.findViewById(R.id.send_form_button);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commentText = input.getText().toString();
                if (commentText.length() == 0)
                {
                    Toast.makeText(getActivity(), "Нельзя добавлять пустой комментарий", Toast.LENGTH_SHORT).show();
                } else
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    setListShown(false);
                    Toast.makeText(getActivity(), "Отправка комментария", Toast.LENGTH_SHORT).show();
                    getRequester().addCommentToWallPost(getGroupId(), post.getId(), commentText, new Requester.RequestResultCallback()
                    {
                        @Override
                        public void pushResult(String result)
                        {
                            if (result == null || (result != null && result.contains("error")))
                            {
                                Toast.makeText(getActivity(), "Не удалось добавить комментарий", Toast.LENGTH_SHORT).show();
                                setListShown(true);
                            } else
                            {
                                stateListener.stateChanged();
                                commentText = null;
                                input.setText("");
                                loadComments();
                            }
                        }
                    });

                }
            }
        });
    }

}
