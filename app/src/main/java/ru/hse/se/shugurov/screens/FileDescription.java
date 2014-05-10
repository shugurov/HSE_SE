package ru.hse.se.shugurov.screens;

/**
 * Class represents a file as a set of two fields: file name and url where this file can be obtained.
 * <p/>
 * Created by Ivan Shugurov
 */
public class FileDescription
{
    private final String url;
    private final String name;

    /**
     * Creates an instance with specified name and url
     *
     * @param name is be used as a file name in appliance storage
     * @param url  is used as a path to a place where this file can be downloaded
     */
    public FileDescription(String name, String url)
    {
        this.name = name;
        this.url = url;
    }

    /**
     * Returns url specified by the constructor
     *
     * @return url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Returns file name specified by the constructor
     *
     * @return name
     */
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
