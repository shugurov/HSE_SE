package ru.hse.se.shugurov.social_networks;

/**
 * Created by Иван on 30.10.13.
 */
public class QueryParameter
{
    public String getParametr()
    {
        return parametr;
    }

    public String getValue()
    {
        return value;
    }

    private String parametr;
    private String value;

    public QueryParameter(String parametr, String value)
    {
        this.parametr = parametr;
        this.value = value;
    }
}
