package moe.vcup.TeeLogin.utils;

import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PasswordManager
{
    public static HashMap<String, String> PasswordBook;
    private static final File PasswordBookFile = new File("./password.yml");
    public final static Logger LOGGER = LogManager.getLogger("teelogin");

    static {
        PasswordBook = new HashMap<>();
        try {
            if (PasswordBookFile.createNewFile()) {
                var fw = new FileWriter(PasswordBookFile);
                fw.write("{}");
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadPasswordBook();
    }

    public static boolean playerIsRegistered(String playerId){
        return PasswordBook.getOrDefault(playerId, null) != null;
    }
    public static boolean PlayerIsRegistered(ServerPlayerEntity player){
        return playerIsRegistered(getPlayerId(player));
    }

    public static boolean PlayerIsNeedCheckPassword(String playerId){
        return PasswordBook.getOrDefault("__"+playerId, null) != null && !playerIsRegistered(playerId);
    }
    public static boolean PlayerIsNeedCheckPassword(ServerPlayerEntity player){
        return PlayerIsNeedCheckPassword(getPlayerId(player));
    }

    public static String getPlayerId(ServerPlayerEntity player){
        return player.getUuidAsString()+"-"+player.getName().asString();
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
        return getPlayerPassword(playerId).equals(password);
    }

    public static boolean verifyPassword(ServerPlayerEntity player, String password){
        return getPlayerPassword(player).equals(password);
    }

    public static boolean setPlayerPassword(String playerId, String password){
        if (PlayerIsNeedCheckPassword(playerId) && verifyPassword(playerId, password)){
            PasswordBook.remove("__"+playerId);
            PasswordBook.put(playerId, password);
        } else if (!playerIsRegistered(playerId)){
            PasswordBook.put("__"+playerId, password);
        } else return false;
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
            LOGGER.warn(e.getMessage());
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
            LOGGER.warn(e.getMessage());
            return false;
        }
        LOGGER.info(PasswordBook);
        return true;
    }
}
