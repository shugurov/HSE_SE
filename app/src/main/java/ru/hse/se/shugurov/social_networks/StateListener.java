package ru.hse.se.shugurov.social_networks;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Иван on 08.05.2014.
 */
public class StateListener implements Serializable
{
    private AtomicBoolean stateFlag;

    public StateListener(AtomicBoolean stateFlag)
    {
        this.stateFlag = stateFlag;
    }

    public void stateChanged()
    {
        stateFlag.set(true);
    }
}
