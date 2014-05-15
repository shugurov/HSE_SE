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
import ru.hse.shugurov.utils.ImageLoader;

/**
 * Used to fill a list of comments
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class CommentsAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private SocialNetworkEntry[] comments;
    private ImageLoader imageLoader = ImageLoader.instance();

    /**
     * Creates a new instance. Makes all preparations for inflating and showing list items
     *
     * @param context  is used to inflate layouts
     * @param comments array of comments to be displayed
     */
    public CommentsAdapter(Context context, SocialNetworkEntry[] comments)
    {
        this.comments = comments;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return comments.length;
    }

    @Override
    public SocialNetworkEntry getItem(int position)
    {
        return comments[position];
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
            convertView = inflater.inflate(R.layout.social_network_comment, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.comment_author_name)).setText(comments[position].getAuthor().getFullName());
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) convertView.findViewById(R.id.footer_date)).setText(format.format(comments[position].getDate()));
        TextView commentTextView = (TextView) convertView.findViewById(R.id.comment_text);
        commentTextView.setText(Html.fromHtml(comments[position].getText()));
        ImageView authorPhoto = (ImageView) convertView.findViewById(R.id.comment_author_photo);
        authorPhoto.setImageBitmap(null);
        float weightSum = ((LinearLayout) convertView).getWeightSum();
        int width = (int) ((parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight()) * (1 / weightSum));
        authorPhoto.getLayoutParams().height = width;
        imageLoader.displayImage(comments[position].getAuthor().getPhoto(), authorPhoto);
        return convertView;
    }
}
