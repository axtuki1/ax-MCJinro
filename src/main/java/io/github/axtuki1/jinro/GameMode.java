package io.github.axtuki1.jinro;

public enum GameMode {
    MinecraftJinro, OneNightJinro;

    private static GameMode CurrentGameMode;

    public static void setGameMode(GameMode gm){
        CurrentGameMode = gm;
        Jinro.getMain().getConfig().set("GameMode", CurrentGameMode.toString());
        Jinro.getMain().saveConfig();
    }

    public static GameMode getGameMode() {
        return CurrentGameMode;
    }

}
