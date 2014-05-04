package ru.hse.se.shugurov.gui;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AccessToken;
import ru.hse.se.shugurov.social_networks.VKAbstractItem;
import ru.hse.se.shugurov.social_networks.VKProfile;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.social_networks.VKResponsesAdapter;
import ru.hse.se.shugurov.social_networks.VKTopic;
import ru.hse.se.shugurov.utills.ImageLoader;

/**
 * Created by Иван on 03.05.2014.
 */
public class WallCommentsScreen extends ListFragment//TODO после поворота экрана нету авы группы
{
    private final String VK_WALL_GROUP_ID = "vk_wall_comments_group_id";
    private final String VK_WALL_COMMENTS_TITLE_TAG = "vk_wall_comments_title";
    private final String ACCESS_TOKEN_TAG = "vk_wall_comments_access_token";
    private final String VK_WALL_COMMENTS_TAG = "vk_wall_comments";
    private final String VK_WALL_COMMENTS_POST_TAG = "vk_wall_comments_post";
    private String groupId;
    private VKTopic post;
    private String title;
    private AccessToken accessToken;
    private VKAbstractItem[] comments;
    private int containerWidth;

    public WallCommentsScreen()
    {

    }

    public WallCommentsScreen(String groupId, VKTopic post, String title, AccessToken accessToken)
    {
        this.groupId = groupId;
        this.post = post;
        this.title = title;
        this.accessToken = accessToken;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && accessToken == null)
        {
            groupId = savedInstanceState.getString(VK_WALL_GROUP_ID);
            post = savedInstanceState.getParcelable(VK_WALL_COMMENTS_POST_TAG);
            title = savedInstanceState.getString(VK_WALL_COMMENTS_TITLE_TAG);
            accessToken = (AccessToken) savedInstanceState.getSerializable(ACCESS_TOKEN_TAG);
            comments = (VKAbstractItem[]) savedInstanceState.getParcelableArray(VK_WALL_COMMENTS_TAG);
        }
    }

    //BEGIN TODO тупо скопипастил код для получения ширины экрана
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

    //END TODO

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);//TODO Do I have to do it here?
        containerWidth = container.getWidth() - container.getPaddingLeft() - container.getPaddingRight();
        if (containerWidth == 0)
        {
            containerWidth = getScreenWidth() - container.getPaddingLeft() - container.getPaddingRight();
        }
        getActivity().setTitle(title);
        return resultView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (comments == null)
        {
            VKRequester requester = new VKRequester(accessToken);
            requester.getWallComments(groupId, post.getId(), new Requester.RequestResultCallback()
            {
                @Override
                public void pushResult(String result)
                {
                    if (result == null)
                    {
                        Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        comments = VKRequester.getWallComments(result);//TODo parse in different thread
                        VKRequester.getProfileInformation(comments, new Requester.RequestResultCallback()
                        {
                            @Override
                            public void pushResult(String result)
                            {
                                handleFullProfilesInformationObtaining(result);
                            }
                        });
                    }
                }
            });
        } else
        {
            boolean emptyProfilesOccur = false;
            for (int i = 0; i < comments.length; i++)
            {
                if (comments[i].getAuthor().getFullName() == null)
                {
                    VKRequester.getProfileInformation(comments, new Requester.RequestResultCallback()
                    {
                        @Override
                        public void pushResult(String result)//TODo parse in different thread
                        {
                            handleFullProfilesInformationObtaining(result);
                        }
                    });
                    emptyProfilesOccur = true;
                    break;
                }
            }
            if (!emptyProfilesOccur)
            {
                instantiateAdapter();
            }
        }
    }

    private void handleFullProfilesInformationObtaining(String result)
    {
        if (result == null)
        {
            Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
        } else
        {
            VKRequester.fillProfileInformation(comments, result);
            instantiateAdapter();
        }
    }

    private void instantiateAdapter()
    {
        VKResponsesAdapter responsesAdapter = new VKResponsesAdapter(getActivity(), comments);
        View headerView = createHeaderView();
        getListView().addHeaderView(headerView);
        setListAdapter(responsesAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ACCESS_TOKEN_TAG, accessToken);
        outState.putParcelableArray(VK_WALL_COMMENTS_TAG, comments);
        outState.putString(VK_WALL_COMMENTS_TITLE_TAG, title);
        outState.putString(VK_WALL_GROUP_ID, groupId);
        outState.putParcelable(VK_WALL_COMMENTS_POST_TAG, post);
    }


    private View createHeaderView()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View resultView = inflater.inflate(R.layout.vk_wall_post, null, false);
        ImageLoader imageLoader = ImageLoader.instance();
        VKProfile author = post.getAuthor();
        ImageView authorPhoto = (ImageView) resultView.findViewById(R.id.vk_post_author_photo);
        authorPhoto.setImageBitmap(null);
        float weightSum = ((LinearLayout) resultView).getWeightSum();
        int photoWidth = (int) (containerWidth * (1 / weightSum));
        FlexibleImageView authorPhotoProxy = new FlexibleImageView(authorPhoto, photoWidth);
        imageLoader.displayImage(author.getPhoto(), authorPhotoProxy);
        ImageView attachedPicture = (ImageView) resultView.findViewById(R.id.vk_wall_attached_picture);
        attachedPicture.setImageBitmap(null);
        if (post.getAttachedPicture() != null)
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            photoWidth = containerWidth - photoWidth;
            FlexibleImageView attachedImageProxy = new FlexibleImageView(attachedPicture, photoWidth);
            imageLoader.displayImage(post.getAttachedPicture(), attachedImageProxy);
        } else
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        ((TextView) resultView.findViewById(R.id.vk_wall_post_author_name)).setText(author.getFullName());
        ((TextView) resultView.findViewById(R.id.vk_wall_post_text)).setText(Html.fromHtml(post.getText()));
//        ((TextView)convertView.findViewById(R.id.vk_comments_quantity)).setText(currentPost.getComments()); //TODO it does not work... why???
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) resultView.findViewById(R.id.vk_date)).setText(format.format(post.getDate()));
        return resultView;
    }

}
