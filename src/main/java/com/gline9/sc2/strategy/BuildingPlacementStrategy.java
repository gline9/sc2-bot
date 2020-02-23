package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.gline9.sc2.conglomerates.BasePoint;

public interface BuildingPlacementStrategy {
    Point2d getLocationForStructure(Unit unit, Ability abilityForStructure, BasePoint base);
}
