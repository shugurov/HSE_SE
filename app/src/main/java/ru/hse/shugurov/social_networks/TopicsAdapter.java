package ru.hse.shugurov.social_networks;

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

import ru.hse.shugurov.R;
import ru.hse.shugurov.utills.ImageLoader;


/**
 * Used to create a list of topics
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class TopicsAdapter extends BaseAdapter
{
    private SocialNetworkTopic[] topics;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.instance();
    private DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private boolean doShowCommentsQuantity;

    /**
     * Creates a new instance. Makes all preparations for inflating and showing list items
     *
     * @param context                is used to inflate layouts
     * @param topics                 array of topics to be displayed
     * @param doShowCommentsQuantity specifies if a number of comments os shown
     */
    public TopicsAdapter(Context context, SocialNetworkTopic[] topics, boolean doShowCommentsQuantity)
    {
        this.topics = topics;
        inflater = LayoutInflater.from(context);
        this.doShowCommentsQuantity = doShowCommentsQuantity;
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
        if (doShowCommentsQuantity)
        {
            ((TextView) convertView.findViewById(R.id.footer_comments_quantity)).setText(currentTopic.getCommentsString());
        }
        String date = format.format(getItem(position).getDate());
        ((TextView) convertView.findViewById(R.id.footer_date)).setText(date);
        return convertView;
    }
}
