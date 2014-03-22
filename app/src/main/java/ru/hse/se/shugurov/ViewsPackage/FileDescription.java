package ru.hse.se.shugurov.ViewsPackage;

/**
 * Created by Иван on 25.10.13.
 */
public class FileDescription
{
    private String url;
    private String name;

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
}
