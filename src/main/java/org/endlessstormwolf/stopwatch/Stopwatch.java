package org.endlessstormwolf.stopwatch;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stopwatch implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("wolfsstopwatch");

	public static KeyBinding stopStart;
	public static KeyBinding reset;

	@Override
	public void onInitialize() {

		LOGGER.info("Stopwatch initializing!");
		stopStart = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.stopwatch.stopstart", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_R, // The keycode of the key
				"category.stopwatch.stopwatch"// The translation key of the keybinding's category.
		));
		reset = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.stopwatch.reset", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_G, // The keycode of the key
				"category.stopwatch.stopwatch"// The translation key of the keybinding's category.
		));
	}
}