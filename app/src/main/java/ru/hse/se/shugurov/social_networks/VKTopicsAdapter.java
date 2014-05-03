package ru.hse.se.shugurov.social_networks;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.utills.ImageLoader;


/**
 * Created by Иван on 13.02.14.
 */
public class VKTopicsAdapter extends BaseAdapter
{
    private VKTopic[] topics;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = ImageLoader.instance();
    private DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public VKTopicsAdapter(Context context, VKTopic[] topics)
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
    public VKTopic getItem(int position)
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
            convertView = inflater.inflate(R.layout.vk_topic, parent, false);
        }
        ImageView authorPhoto = (ImageView) convertView.findViewById(R.id.vk_topic_author_photo);
        authorPhoto.setImageBitmap(null);
        imageLoader.displayImage(topics[position].getAuthor().getPhoto(), authorPhoto);
        ((TextView) convertView.findViewById(R.id.vk_topic_author_name)).setText(Html.fromHtml(topics[position].getAuthor().getFullName()));
        ((TextView) convertView.findViewById(R.id.vk_topic_text)).setText(Html.fromHtml(topics[position].getText()));
        ((TextView) convertView.findViewById(R.id.vk_comments_quantity)).setText(Integer.toString(topics[position].getComments()));
        String date = format.format(getItem(position).getDate());
        ((TextView) convertView.findViewById(R.id.vk_date)).setText(date);
        return convertView;
    }

    private String getCorrectWordForComment(int numberOfComments)
    {
        int lastFigure = numberOfComments % 100;
        switch (lastFigure)
        {
            case 2://TODO fix these dreadful lines
            case 3:
            case 4:
            case 32:
            case 42:
            case 52:
            case 62:
            case 72:
            case 82:
            case 92:
            case 33:
            case 43:
            case 34:
            case 53:
            case 44:
            case 54:
            case 64:
            case 74:
            case 84:
            case 94:
                return " комментария";
            case 0:
            case 10:
            case 20:
            case 30:
            case 40:
            case 50:
            case 60:
            case 70:
            case 80:
            case 90:

            case 5:
            case 15:
            case 25:
            case 35:
            case 45:
            case 55:
            case 65:
            case 75:
            case 85:
            case 95:


            case 6:
            case 16:
            case 26:
            case 36:
            case 46:
            case 56:
            case 66:
            case 76:
            case 86:
            case 96:

            case 7:
            case 17:
            case 27:
            case 37:
            case 47:
            case 57:
            case 67:
            case 87:
            case 97:
            case 8:
            case 9:

            case 11:
            case 12:
            case 13:
            case 14:
            case 18:
            case 19:
            case 28:
            case 29:
            case 38:
            case 39:
            case 48:
            case 49:
            case 58:
            case 59:
                return " комментариев";
            case 1:
            case 21:
            case 31:
            case 41:
            case 51:
            case 61:
            case 71:
            case 81:
            case 91:
                return " комментарий";
            case 22:
            case 23:
            case 24:
                return " комментария";
            case 63:
                return "";
            case 68:
                return "";
            case 69:
                return "";
            case 73:
                return "";
            case 77:
                return "";
            case 78:
                return "";
            case 79:
                return "";
            case 83:
                return "";
            case 88:
                return "";
            case 89:
                return "";
            case 93:
                return "";
            case 98:
                return "";
            case 99:
                return "";

        }
        return "";
    }
}
