package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Map;

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
public class WallCommentsScreen extends ListFragment
{
    private String groupId;
    private VKTopic post;
    private String title;
    private AccessToken accessToken;
    private Map<Integer, VKProfile> profilesMap;
    private VKAbstractItem[] comments;

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        getActivity().setTitle(title);
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
                            if (result == null)
                            {
                                Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                            } else
                            {
                                VKRequester.fillProfileInformation(comments, result);
                                VKResponsesAdapter responsesAdapter = new VKResponsesAdapter(getActivity(), comments);
                                View headerView = createHeaderView(container);
                                getListView().addHeaderView(headerView);
                                setListAdapter(responsesAdapter);
                            }
                        }
                    });
                }
            }
        });
        return resultView;
    }


    private View createHeaderView(ViewGroup container)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View resultView = inflater.inflate(R.layout.vk_comment, null, false);
        ((TextView) resultView.findViewById(R.id.vk_comment_author_name)).setText(post.getAuthor().getFullName());
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) resultView.findViewById(R.id.vk_comment_date)).setText(format.format(post.getDate()));
        ((TextView) resultView.findViewById(R.id.vk_comment_text)).setText(Html.fromHtml(post.getText()));
        ImageView authorPhoto = (ImageView) resultView.findViewById(R.id.vk_comment_author_photo);
        float weightSum = ((LinearLayout) resultView).getWeightSum();
        int width = (int) ((container.getWidth() - container.getPaddingLeft() - container.getPaddingRight()) * (1 / weightSum));
        FlexibleImageView flexibleImage = new FlexibleImageView(authorPhoto, width);
        ImageLoader.instance().displayImage(post.getAuthor().getPhoto(), flexibleImage);
        return resultView;
    }


}
