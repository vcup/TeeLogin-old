package moe.vcup.TeeLogin.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import moe.vcup.TeeLogin.Callbacks.PlayerLoginCallback;
import moe.vcup.TeeLogin.TeeLogin;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;


public final class LoginManager{
    public static Logger LOGGER = TeeLogin.LOGGER;
    public static HashSet<ServerPlayerEntity> LoggedPlayers;
    public static HashMap<String, Location> PlayerLocations;
    public static File LocationFile = new File("./config/TeeLogin/location.json");
    public static Gson gson = new Gson();

    static {
        LoggedPlayers = new HashSet<>();
        PlayerLocations = new HashMap<>();
        PlayerLocations.put("server", new Location(0, 0, 0, 0, 0, GameMode.DEFAULT, World.OVERWORLD.getValue().toString()));
        try {
            if (LocationFile.createNewFile()){
                var json = new Gson().toJson(PlayerLocations);
                var fw = new FileWriter(LocationFile);
                fw.write(json);
                fw.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        loadLocation();
    }

    public static class Location{
        public Location(double x, double y, double z, float yaw, float pitch, GameMode gameMode, String w){
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
            this.yaw = yaw;
            this.pitch = pitch;
            this.gameMode = gameMode;
            this.w = w;
        }
        public double x, y, z;
        public float yaw, pitch;
        public GameMode gameMode;
        public String w;
    }

    public static boolean playerIsLogged(ServerPlayerEntity player){
        return LoggedPlayers.contains(player);
    }

    public static Location getPlayerLocation(ServerPlayerEntity player){
        return PlayerLocations.getOrDefault(getPlayerId(player), null);
    }

    public static void setPlayerLocation(ServerPlayerEntity player){
        if (!player.isDead()){
        PlayerLocations.put(getPlayerId(player),
                new Location(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(),
                        player.interactionManager.getGameMode(), player.getWorld().getRegistryKey().getValue().toString())
        );
        } else {
            var spawnPoint = player.getSpawnPointPosition();
            spawnPoint = spawnPoint == null ? player.getWorld().getServer().getWorld(player.getSpawnPointDimension()).getSpawnPos() : spawnPoint;
            var playerRespawnWorldId = player.getSpawnPointDimension();
            PlayerLocations.put(getPlayerId(player),
                new Location(spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ(), player.getYaw(), player.getPitch(),
                        player.interactionManager.getGameMode(), playerRespawnWorldId.getValue().toString())
            );
        }
        saveLocation();
    }

    public static void teleportPlayerToLocation(ServerPlayerEntity player){
        var location = getPlayerLocation(player);
        if (location != null){
            ServerWorld world = player.getWorld().getServer().getWorld(
                RegistryKey.of(Registry.WORLD_KEY, new Identifier(location.w))
            );
            player.teleport(world, location.x, location.y, location.z, location.yaw, location.pitch);
        }else {
            LOGGER.warn(PlayerLocations);
        }
    }

    public static boolean LoggedPlayer(ServerPlayerEntity player){
        if (playerIsLogged(player)) return false;
        player.changeGameMode(PlayerLocations.get(getPlayerId(player)).gameMode);
        teleportPlayerToLocation(player);
        LoggedPlayers.add(player);
        callbacks.get(player).onLogged();
        return true;
    }

    public static void LoggedOutPlayer(ServerPlayerEntity player){
        if (LoggedPlayers.remove(player)) {
            setPlayerLocation(player);
            callbacks.get(player).onLogout();
        }
    }

    public static void NewLoginPlayer(ServerPlayerEntity player){
        if (PlayerLocations.getOrDefault(getPlayerId(player), null) == null){
            setPlayerLocation(player);
        }
        player.changeGameMode(GameMode.SPECTATOR);
    }

    public static String getPlayerId(ServerPlayerEntity player){
        return player.getUuidAsString()+"-"+player.getName().asString();
    }

    public static boolean saveLocation(){
        var json = gson.toJson(PlayerLocations);
        try {
            var writer = new FileWriter(LocationFile);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean loadLocation(){
        var type = new TypeToken<HashMap<String, Location>>(){}.getType();
        try {
            var reader = new FileReader(LocationFile);
            PlayerLocations = gson.fromJson(reader, type);
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

    public static void registerCallback(ServerPlayerEntity player, PlayerLoginCallback callback){
        callbacks.put(player, callback);
    }

    private static HashMap<ServerPlayerEntity, PlayerLoginCallback> callbacks = new HashMap<>();
}
