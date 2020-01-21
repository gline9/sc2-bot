package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;

public interface ControllableUnit extends IUnit
{
    void tick(UnitInPool unitInPool);
}
