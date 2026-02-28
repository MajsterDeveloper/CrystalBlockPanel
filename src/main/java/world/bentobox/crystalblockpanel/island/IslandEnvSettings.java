package world.bentobox.crystalblockpanel.island;

public class IslandEnvSettings {

    private boolean normalTime = true;
    private boolean eternalDay = false;
    private boolean eternalNight = false;

    private boolean normalWeather = true;
    private boolean eternalSun = false;
    private boolean eternalRain = false;

    private boolean itemPickupAllowed = true;
    private boolean mobDamageEnabled = true;
    private boolean animalDamageEnabled = true;

    private boolean spawnerOnlyMobs;

    private boolean buttonsAllowed = true;
    private boolean pressurePlatesAllowed = true;
    private boolean leversAllowed = true;

    public boolean isNormalTime() { return normalTime; }
    public void setNormalTime(boolean normalTime) {
        this.normalTime = normalTime;
        if(eternalDay) eternalDay = false;
        if(eternalNight) eternalNight = false;
    }

    public boolean isEternalDay() { return eternalDay; }
    public void setEternalDay(boolean eternalDay) {
        this.eternalDay = eternalDay;
        if(eternalNight) eternalNight = false;
        if(normalTime) normalTime = false;
    }

    public boolean isEternalNight() { return eternalNight; }
    public void setEternalNight(boolean eternalNight) {
        this.eternalNight = eternalNight;
        if(eternalDay) eternalDay = false;
        if(normalTime) normalTime = false;
    }

    public boolean isNormalWeather() { return normalWeather; }
    public void setNormalWeather(boolean normalWeather) {
        this.normalWeather = normalWeather;
        if(eternalSun) eternalSun = false;
        if(eternalRain) eternalRain = false;
    }

    public boolean isEternalSun() { return eternalSun; }
    public void setEternalSun(boolean eternalSun) {
        this.eternalSun = eternalSun;
        if(eternalRain) eternalRain = false;
        if(normalWeather) normalWeather = false;
    }

    public boolean isEternalRain() { return eternalRain; }
    public void setEternalRain(boolean eternalRain) {
        this.eternalRain = eternalRain;
        if(eternalSun) eternalSun = false;
        if(normalWeather) normalWeather = false;
    }

    public boolean isItemPickupAllowed() { return itemPickupAllowed; }
    public void setItemPickupAllowed(boolean itemPickupAllowed) { this.itemPickupAllowed = itemPickupAllowed; }

    public boolean isMobDamageEnabled() { return mobDamageEnabled; }
    public void setMobDamageEnabled(boolean mobDamageEnabled) { this.mobDamageEnabled = mobDamageEnabled; }

    public boolean isAnimalDamageEnabled() { return animalDamageEnabled; }
    public void setAnimalDamageEnabled(boolean animalDamageEnabled) { this.animalDamageEnabled = animalDamageEnabled; }

    public boolean isSpawnerOnlyMobs() { return spawnerOnlyMobs; }
    public void setSpawnerOnlyMobs(boolean spawnerOnlyMobs) { this.spawnerOnlyMobs = spawnerOnlyMobs; }

    public boolean isButtonsAllowed() { return buttonsAllowed; }
    public void setButtonsAllowed(boolean buttonsAllowed) { this.buttonsAllowed = buttonsAllowed; }

    public boolean isPressurePlatesAllowed() { return pressurePlatesAllowed; }
    public void setPressurePlatesAllowed(boolean pressurePlatesAllowed) { this.pressurePlatesAllowed = pressurePlatesAllowed; }

    public boolean isLeversAllowed() { return leversAllowed; }
    public void setLeversAllowed(boolean leversAllowed) { this.leversAllowed = leversAllowed; }
}