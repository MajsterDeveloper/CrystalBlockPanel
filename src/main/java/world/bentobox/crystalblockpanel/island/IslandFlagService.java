package world.bentobox.crystalblockpanel.island;

import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.managers.RanksManager;
import world.bentobox.bentobox.lists.Flags;

public class IslandFlagService {

    private static final int ENABLE_ALL = RanksManager.VISITOR_RANK; // 0
    private static final int DISABLED   = -1;

    public enum SpawnMode {
        NATURAL_AND_SPAWNER, // oba ON
        SPAWNER_ONLY,        // natural OFF, spawner ON
        NATURAL_ONLY,        // natural ON,  spawner OFF
        OFF                  // oba OFF
    }


    // 5 rang – od najbardziej liberalnej do najbardziej restrykcyjnej
    // (to są progi "minimalnej rangi" w BentoBox)
    public static final int[] RANK_CYCLE = new int[]{
            RanksManager.VISITOR_RANK,
            RanksManager.COOP_RANK,
            RanksManager.TRUSTED_RANK,
            RanksManager.MEMBER_RANK,
            RanksManager.OWNER_RANK
    };

    public int getMinRank(@NotNull Island island, @NotNull Flag flag) {
        return island.getFlag(flag);
    }

    public int cycleMinRank(@NotNull Island island, @NotNull Flag flag) {
        int current = island.getFlag(flag);

        int idx = indexOfRank(current);
        int nextIdx = (idx + 1) % RANK_CYCLE.length;

        int next = RANK_CYCLE[nextIdx];
        island.setFlag(flag, next);
        return next;
    }

    private int indexOfRank(int rank) {
        // jeśli trafi jakaś niestandardowa wartość, dobierz najbliższy “koszyk”
        // (np. 0/200/400/500/1000 etc.)
        int bestIdx = 0;
        int bestDiff = Integer.MAX_VALUE;

        for (int i = 0; i < RANK_CYCLE.length; i++) {
            int diff = Math.abs(RANK_CYCLE[i] - rank);
            if (diff < bestDiff) {
                bestDiff = diff;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    // ===== MopSpawn =====

    public @NotNull SpawnMode getMonsterSpawnMode(@NotNull Island island) {
        int natural = island.getFlag(Flags.MONSTER_NATURAL_SPAWN);
        int spawner = island.getFlag(Flags.MONSTER_SPAWNERS_SPAWN);

        boolean naturalOn = natural >= ENABLE_ALL;
        boolean spawnerOn = spawner >= ENABLE_ALL;

        if (naturalOn && spawnerOn) return SpawnMode.NATURAL_AND_SPAWNER;
        if (!naturalOn && spawnerOn) return SpawnMode.SPAWNER_ONLY;
        if (naturalOn && !spawnerOn) return SpawnMode.NATURAL_ONLY;
        return SpawnMode.OFF;
    }

    public void setMonsterSpawnMode(@NotNull Island island, @NotNull SpawnMode mode) {
        switch (mode) {
            case NATURAL_AND_SPAWNER -> {
                island.setFlag(Flags.MONSTER_NATURAL_SPAWN, ENABLE_ALL);
                island.setFlag(Flags.MONSTER_SPAWNERS_SPAWN, ENABLE_ALL);
            }
            case SPAWNER_ONLY -> {
                island.setFlag(Flags.MONSTER_NATURAL_SPAWN, DISABLED);
                island.setFlag(Flags.MONSTER_SPAWNERS_SPAWN, ENABLE_ALL);
            }
            case NATURAL_ONLY -> {
                island.setFlag(Flags.MONSTER_NATURAL_SPAWN, ENABLE_ALL);
                island.setFlag(Flags.MONSTER_SPAWNERS_SPAWN, DISABLED);
            }
            case OFF -> {
                island.setFlag(Flags.MONSTER_NATURAL_SPAWN, DISABLED);
                island.setFlag(Flags.MONSTER_SPAWNERS_SPAWN, DISABLED);
            }
        }
    }

    public @NotNull SpawnMode cycleMonsterSpawnMode(@NotNull Island island) {
        SpawnMode current = getMonsterSpawnMode(island);
        SpawnMode next = switch (current) {
            case NATURAL_AND_SPAWNER -> SpawnMode.SPAWNER_ONLY;
            case SPAWNER_ONLY -> SpawnMode.NATURAL_ONLY;
            case NATURAL_ONLY -> SpawnMode.OFF;
            case OFF -> SpawnMode.NATURAL_AND_SPAWNER;
        };
        setMonsterSpawnMode(island, next);
        return next;
    }

    // Jeżeli chcesz też zwierzęta analogicznie:
    public void setAnimalSpawnMode(@NotNull Island island, @NotNull SpawnMode mode) {
        switch (mode) {
            case NATURAL_AND_SPAWNER -> {
                island.setFlag(Flags.ANIMAL_NATURAL_SPAWN, ENABLE_ALL);
                island.setFlag(Flags.ANIMAL_SPAWNERS_SPAWN, ENABLE_ALL);
            }
            case SPAWNER_ONLY -> {
                island.setFlag(Flags.ANIMAL_NATURAL_SPAWN, DISABLED);
                island.setFlag(Flags.ANIMAL_SPAWNERS_SPAWN, ENABLE_ALL);
            }
            case NATURAL_ONLY -> {
                island.setFlag(Flags.ANIMAL_NATURAL_SPAWN, ENABLE_ALL);
                island.setFlag(Flags.ANIMAL_SPAWNERS_SPAWN, DISABLED);
            }
            case OFF -> {
                island.setFlag(Flags.ANIMAL_NATURAL_SPAWN, DISABLED);
                island.setFlag(Flags.ANIMAL_SPAWNERS_SPAWN, DISABLED);
            }
        }
    }
}

