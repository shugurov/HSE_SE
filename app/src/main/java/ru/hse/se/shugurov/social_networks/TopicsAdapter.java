package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.utills.ImageLoader;


/**
 * Created by Иван on 13.02.14.
 */
public class TopicsAdapter extends BaseAdapter
{
    private SocialNetworkTopic[] topics;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.instance();
    private DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public TopicsAdapter(Context context, SocialNetworkTopic[] topics)
    {
        this.topics = topics;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return topics.length;
    }

    @Override
    public SocialNetworkTopic getItem(int position)
    {
        return topics[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.social_network_topic, parent, false);
        }
        SocialNetworkTopic currentTopic = getItem(position);
        SocialNetworkProfile topicAuthor = currentTopic.getAuthor();
        ImageView authorPhoto = (ImageView) convertView.findViewById(R.id.topic_author_photo);
        authorPhoto.setImageBitmap(null);
        float weightSum = ((LinearLayout) convertView.findViewById(R.id.topic_container)).getWeightSum();
        int imageSize = (int) ((parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight()) * (1 / weightSum));
        authorPhoto.getLayoutParams().height = imageSize;
        imageLoader.displayImage(topicAuthor.getPhoto(), authorPhoto);
        ((TextView) convertView.findViewById(R.id.topic_title)).setText(currentTopic.getTitle());
        ((TextView) convertView.findViewById(R.id.topic_author_name)).setText(Html.fromHtml(topicAuthor.getFullName()));
        TextView topicTextView = (TextView) convertView.findViewById(R.id.topic_text);
        topicTextView.setText(Html.fromHtml(currentTopic.getText()));
        topicTextView.setMaxLines(3);
        ((TextView) convertView.findViewById(R.id.footer_comments_quantity)).setText(currentTopic.getCommentsString());
        String date = format.format(getItem(position).getDate());
        ((TextView) convertView.findViewById(R.id.footer_date)).setText(date);
        return convertView;
    }
}
