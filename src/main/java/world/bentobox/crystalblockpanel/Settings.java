package world.bentobox.crystalblockpanel;

import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigObject;

public class Settings implements ConfigObject {

    @ConfigComment("Nazwa świata addonu CrystalBlock")
    @ConfigEntry(path = "world.world-name")
    private String worldName = "oneblock_world";

    @ConfigComment("Ustawia czas według tick dla dnia")
    @ConfigEntry(path = "weather.day-ticks")
    private long dayTicks = 3000;

    @ConfigComment("Ustawia czas według tick dla nocy")
    @ConfigEntry(path = "weather.night-ticks")
    private long nightTicks = 18000;

    public String getWorldName() {
        return worldName;
    }
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public long getDayTicks(){ return dayTicks;}
    public void setDayTicks( long dayTicks ){ this.dayTicks = dayTicks; }

    public long getNightTicks(){ return nightTicks;}
    public void setNightTicks( long nightTicks ){ this.nightTicks = nightTicks;}





}