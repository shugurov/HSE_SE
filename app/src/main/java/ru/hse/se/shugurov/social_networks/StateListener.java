package ru.hse.se.shugurov.social_networks;

import java.io.Serializable;

/**
 * Created by Иван on 08.05.2014.
 */
public interface StateListener extends Serializable//TODO сделать класс и добиться того, чтобы не падал при закрытии комментов
{
    void stateChanged();
}
