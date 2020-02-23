package com.gline9.sc2;

import com.github.ocraft.s2client.bot.S2Coordinator;
import com.github.ocraft.s2client.protocol.game.BattlenetMap;
import com.github.ocraft.s2client.protocol.game.Difficulty;
import com.github.ocraft.s2client.protocol.game.Race;

public class Main
{
    public static void main(String[] args)
    {
        Agent agent = new Agent();
        S2Coordinator coordinator = S2Coordinator.setup()
                .loadSettings(args)
                .setParticipants(
                        S2Coordinator.createParticipant(Race.ZERG, agent),
                        S2Coordinator.createComputer(Race.TERRAN, Difficulty.VERY_EASY)
                ).launchStarcraft()
                .startGame(BattlenetMap.of("Triton LE"));

        while (coordinator.update()) {}

        coordinator.quit();
    }

}
