package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.gline9.sc2.conglomerates.BasePoint;
import io.reactivex.annotations.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UnitPool
{
    private final Map<Long, IUnit> unitIDMap = new HashMap<>();
    private final Map<Class<? extends IUnit>, Set<IUnit>> unitTypeMap = new HashMap<>();
    private final UnitPoolPublisher creationPublisher = new UnitPoolPublisher();
    private final UnitPoolPublisher idlePublisher = new UnitPoolPublisher();
    private final UnitFactory factory;

    public UnitPool(UnitFactory factory)
    {
        this.factory = factory;
    }

    public IUnit ensureUnit(UnitInPool unitInPool)
    {
        Unit unit = unitInPool.unit();
        Long tag = unit.getTag().getValue();

        if (unitIDMap.containsKey(tag))
        {
            IUnit iunit = unitIDMap.get(tag);
            iunit.setUnit(unit);

            return iunit;
        }

        IUnit iunit = factory.createUnit(unitInPool, this);
        if (null == iunit)
        {
            return null;
        }

        iunit.setUnit(unitInPool.unit());

        unitIDMap.put(tag, iunit);
        Class<? extends IUnit> clazz = iunit.getClass();

        registerForClass((Class)clazz, iunit); // TODO fix this cast some how

        return iunit;
    }

    @SuppressWarnings("unchecked")
    private void registerForClass(Class<? extends IUnit> clazz, IUnit unit)
    {
        runForAllSubtypes(clazz, unit, this::addToUnitTypeMap);
    }

    @SuppressWarnings("unchecked")
    private void addToUnitTypeMap(Class<? extends IUnit> clazz, IUnit unit)
    {
        // TODO double check this is the correct ordering
        creationPublisher.emitForType((Class)clazz, unit);
        unitTypeMap.computeIfAbsent(clazz, (__) -> new HashSet<>()).add(unit);
    }

    @SuppressWarnings("unchecked")
    public <U> Set<? extends U> getUnitsOfType(Class<U> clazz)
    {
        Set<? extends U> ret = (Set<? extends U>) unitTypeMap.get(clazz);

        return ret == null ? Collections.emptySet() : ret;
    }

    public <U extends IUnit> void subscribeToUnitCreation(Class<U> clazz, Consumer<? super U> consumer)
    {
        creationPublisher.subscribeToUnitType(clazz, consumer);
    }

    public void onUnitIdle(UnitInPool unitInPool)
    {
        IUnit unit = ensureUnit(unitInPool);

        if (null == unit)
        {
            return;
        }

        unit.onIdle();
    }

    public void onUnitDestroyed(UnitInPool unitInPool)
    {
        long id = unitInPool.getTag().getValue();

        IUnit unit = unitIDMap.remove(id);

        if (null == unit)
        {
            return;
        }

        unitTypeMap.forEach((key, value) -> value.remove(unit));
    }

    public <T extends IUnit> List<T> getNClosestUnits(Point2d point, Class<T> clazz, int number)
    {
        Set<? extends T> units = this.getUnitsOfType(clazz);
        return units.stream().sorted(Comparator.comparing(unit -> unit.getUnit().getPosition().toPoint2d().distance(point))).limit(number).collect(Collectors.toList());
    }

    private void runForAllSubtypes(Class<? extends IUnit> clazz, IUnit t, BiConsumer<Class<? extends IUnit>, IUnit> consumer)
    {
        consumer.accept(clazz, t);

        Class<?> superclass = clazz.getSuperclass();

        if (null != superclass && IUnit.class.isAssignableFrom(superclass))
        {
            runForAllSubtypes((Class<? extends IUnit>)superclass, t, consumer);
        }

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> face : interfaces)
        {
            if (IUnit.class.isAssignableFrom(face))
            {
                runForAllSubtypes((Class<? extends IUnit>)face, t, consumer);
            }
        }
    }

}
