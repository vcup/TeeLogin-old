package moe.vcup.TeeLogin.utils;

import com.google.gson.Gson;
import moe.vcup.TeeLogin.TeeLogin;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class PasswordManager
{
    public static HashMap<String, String> PasswordBook;
    private static final File PasswordBookFile = new File("./config/TeeLogin/password.json");
    public static Logger LOGGER = TeeLogin.LOGGER;

    static {
        PasswordBook = new HashMap<>();
        try {
            if (PasswordBookFile.createNewFile()) {
                var fw = new FileWriter(PasswordBookFile);
                fw.write("{}");
                fw.close();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        loadPasswordBook();
    }

    public static boolean playerIsRegistered(String playerId){
        return PasswordBook.getOrDefault(playerId, null) != null;
    }
    public static boolean playerIsRegistered(ServerPlayerEntity player){
        return playerIsRegistered(getPlayerId(player));
    }

    public static boolean playerIsNeedCheckPassword(String playerId){
        return PasswordBook.getOrDefault("__"+playerId, null) != null && !playerIsRegistered(playerId);
    }
    public static boolean playerIsNeedCheckPassword(ServerPlayerEntity player){
        return playerIsNeedCheckPassword(getPlayerId(player));
    }

    public static String getPlayerId(ServerPlayerEntity player){
        return player.getUuidAsString()+"-"+player.getName().getContent();
    }

    public static String getPlayerPassword(String playerId){
        var password = PasswordBook.getOrDefault(playerId, null);
        if (password == null)
            password = PasswordBook.getOrDefault("__"+playerId, null);
        return password;
    }

    public static String getPlayerPassword(ServerPlayerEntity player){
        return getPlayerPassword(getPlayerId(player));
    }

    public static boolean verifyPassword(String playerId, String password){
        return Objects.equals(getPlayerPassword(playerId), password);
    }

    public static boolean verifyPassword(ServerPlayerEntity player, String password){
        return Objects.equals(getPlayerPassword(player), password);
    }

    public static boolean setPlayerPassword(String playerId, String password){
        if (playerIsRegistered(playerId)) return false;
        if (playerIsNeedCheckPassword(playerId)){
            if (verifyPassword(playerId, password)){
                PasswordBook.remove("__"+playerId);
                PasswordBook.put(playerId, password);
            } else {
                PasswordBook.remove("__"+playerId);
                savePasswordBook();
                return false;
            }
        } else {
            PasswordBook.put("__"+playerId, password);
        }
        return savePasswordBook();
    }

    public static boolean setPlayerPassword(ServerPlayerEntity player, String password){
        return setPlayerPassword(getPlayerId(player), password);
    }

    private static boolean savePasswordBook(){
        var json = new Gson().toJson(PasswordBook);
        try {
            var writer = new FileWriter(PasswordBookFile);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean loadPasswordBook(){
        var gson = new Gson();
        try {
            var reader = new FileReader(PasswordBookFile);
            PasswordBook = gson.fromJson(reader, PasswordBook.getClass());
            reader.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }
}
