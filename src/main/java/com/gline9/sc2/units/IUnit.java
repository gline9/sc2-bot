package com.gline9.sc2.units;

import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;

public interface IUnit
{
    Unit getUnit();

    void setUnit(Unit unit);

    default Point2d getLocation()
    {
        return getUnit().getPosition().toPoint2d();
    }

    default void onIdle()
    {
        // default do nothing
    }
}
