package world.bentobox.crystalblockpanel.island;

public class IslandSettingsService {

    private final IslandSettingsRepository repo;

    public IslandSettingsService(IslandSettingsRepository repo) {
        this.repo = repo;
    }

    public IslandEnvSettings env(String islandId) {
        return repo.getEnv(islandId);
    }

    public void set(String islandId, IslandEnvSettings env) {
        repo.setEnv(islandId, env);
    }
}