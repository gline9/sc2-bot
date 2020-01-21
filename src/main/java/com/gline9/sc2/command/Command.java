package com.gline9.sc2.command;

import com.gline9.sc2.units.ControllableUnit;

public interface Command<T extends ControllableUnit>
{
    boolean handle(T unit);
}
