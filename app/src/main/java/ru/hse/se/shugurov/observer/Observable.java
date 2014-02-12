package ru.hse.se.shugurov.observer;

/**
 * Created by Иван on 22.10.13.
 */
public interface Observable
{
    public void addObserver(Observer observer);

    public void removeObserver(Observer observer);

    public void notifyObservers();
}
