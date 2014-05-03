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
import ru.hse.se.shugurov.gui.FlexibleImageView;
import ru.hse.se.shugurov.utills.ImageLoader;

/**
 * Created by Иван on 02.05.2014.
 */
public class VkWallPostsAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.instance();
    private VKTopic[] posts;
    private DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public VkWallPostsAdapter(Context context, VKTopic[] posts)
    {
        this.posts = posts;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return posts.length;
    }

    @Override
    public VKTopic getItem(int position)
    {
        return posts[position];
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
            convertView = inflater.inflate(R.layout.vk_wall_post, parent, false);
        }
        VKTopic currentPost = getItem(position);
        VKProfile author = currentPost.getAuthor();
        ImageView authorPhoto = (ImageView) convertView.findViewById(R.id.vk_post_author_photo);
        authorPhoto.setImageBitmap(null);
        float weightSum = ((LinearLayout) convertView).getWeightSum();
        int width = (int) ((parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight()) * (1 / weightSum));
        FlexibleImageView authorPhotoProxy = new FlexibleImageView(authorPhoto, width);
        imageLoader.displayImage(author.getPhoto(), authorPhotoProxy);
        ImageView attachedPicture = (ImageView) convertView.findViewById(R.id.vk_wall_attached_picture);
        attachedPicture.setImageBitmap(null);
        if (currentPost.getAttachedPicture() != null)
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            width = (int) ((parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight()) * ((weightSum - 1) / weightSum));
            FlexibleImageView attachedImageProxy = new FlexibleImageView(attachedPicture, width);
            imageLoader.displayImage(currentPost.getAttachedPicture(), attachedImageProxy);
        } else
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        ((TextView) convertView.findViewById(R.id.vk_wall_post_author_name)).setText(author.getFullName());
        ((TextView) convertView.findViewById(R.id.vk_wall_post_text)).setText(Html.fromHtml(currentPost.getText()));
//        ((TextView)convertView.findViewById(R.id.vk_comments_quantity)).setText(currentPost.getComments()); //TODO it does not work... why???
        ((TextView) convertView.findViewById(R.id.vk_date)).setText(format.format(currentPost.getDate()));
        return convertView;
    }
}
