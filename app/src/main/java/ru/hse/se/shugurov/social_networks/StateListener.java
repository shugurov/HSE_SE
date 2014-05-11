package ru.hse.se.shugurov.social_networks;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used as a callback object to notify about state changes
 * <p/>
 * Created by Ivan Shugurov
 */
public class StateListener implements Serializable
{
    private AtomicBoolean stateFlag;

    /**
     * creates a new object
     *
     * @param stateFlag flag which shows if changes occur
     */
    public StateListener(AtomicBoolean stateFlag)
    {
        this.stateFlag = stateFlag;
    }

    /**
     * notifies about a change
     */
    public void stateChanged()
    {
        stateFlag.set(true);
    }
}
