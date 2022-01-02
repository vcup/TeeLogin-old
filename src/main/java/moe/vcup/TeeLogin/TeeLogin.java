package moe.vcup.TeeLogin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moe.vcup.TeeLogin.utils.Messenger;
import moe.vcup.TeeLogin.utils.PasswordManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;
import java.util.HashMap;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;


public class TeeLogin implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("teelogin");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
			dispatcher.register(literal("login")
				.then(argument("password", string())
					.executes(this::LoginCommand)
					.then(literal("register")
						.executes(this::RegisterCommand)
					)
				)
			);
		}));
	}

	public int LoginCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		var player = context.getSource().getPlayer();
		var password = getString(context, "password");
		if (!PasswordManager.PlayerIsRegistered(player)){
			Messenger.m(player, true, "首先通过/login <password> register注册！");
		} else if (PasswordManager.PlayerIsNeedCheckPassword(player)){
			Messenger.m(player, true, "再次输入/login <password> register以确认密码！");
		}else if (PasswordManager.verifyPassword(player, password)){
			Messenger.m(player, true, "登录成功！");
		} else {
			Messenger.m(player, true, "密码错误！");
		}
		//player.onLanding();
		return 0;
	}

	public int RegisterCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		var player = context.getSource().getPlayer();
		var password = getString(context, "password");
		if (PasswordManager.PlayerIsRegistered(player)){
			Messenger.m(player, true, "你已注册！通过/login <password> 登入游戏，如需修改密码请联系管理员");
		} else if (PasswordManager.PlayerIsNeedCheckPassword(player)){
			if (PasswordManager.setPlayerPassword(player, password)){
				Messenger.m(player, true, "注册成功！使用/login <password> 登入游戏！");
			} else {
				Messenger.m(player, true, "注册失败！两次输入密码不一致！");
			}
		} else {
			PasswordManager.setPlayerPassword(player, password);
			Messenger.m(player, true, "再次输入/login <password> register 以验证密码");
		}
		return 0;
	}
}
