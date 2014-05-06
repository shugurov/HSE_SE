package ru.hse.se.shugurov.screens;

/**
 * Created by Иван on 25.10.13.
 */
public class FileDescription
{
    private final String url;
    private final String name;

    public FileDescription(String name, String url)
    {
        this.name = name;
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        FileDescription that = (FileDescription) o;

        if (!name.equals(that.name))
        {
            return false;
        }
        if (!url.equals(that.url))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = url.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
