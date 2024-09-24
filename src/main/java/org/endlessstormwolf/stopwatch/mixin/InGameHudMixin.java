package org.endlessstormwolf.stopwatch.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import org.endlessstormwolf.stopwatch.Stopwatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Unique
	private long timer = 0;
	@Unique
	private long timeStart = 0;

	@Unique
	private long timeOld = 0;

	@Unique
	private boolean timerActive = false;

	@Unique
	private boolean overlayActive = true;
	@Shadow @Final
	private MinecraftClient client;

	//@Shadow
	private int scaledWidth;

	//@Shadow
	private int scaledHeight;
	@Shadow public TextRenderer getTextRenderer() {
		return this.client.textRenderer;
	}

	/*@Shadow private void drawTextBackground(DrawContext context, TextRenderer textRenderer, int yOffset, int width, int color) {
		int i = this.client.options.getTextBackgroundColor(0.0F);
		if (i != 0) {
			int j = -width / 2;
			int var10001 = j - 2;
			int var10002 = yOffset - 2;
			int var10003 = j + width + 2;
			Objects.requireNonNull(textRenderer);
			context.fill(var10001, var10002, var10003, yOffset + 9 + 2, ColorHelper.Argb.mixColor(i, color));
		}
	}*/

	@Unique
	private String prepString(long num) {
		String timeString;
		if (num < 10) {
			timeString = "0" + num;
		} else {
			timeString = String.valueOf(num);
		}
		return timeString;
	}

	@Inject(method = "renderMainHud", at = @At("TAIL"))
	public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		scaledWidth = context.getScaledWindowWidth();
		scaledHeight = context.getScaledWindowHeight();
		float scale = (float) scaledWidth/320;
		if (Stopwatch.reset.wasPressed()) {
			Stopwatch.LOGGER.info("Stopwatch reset at: " + getTime());
			if (!overlayActive) {
				overlayActive = true;
			} else {
				if (timer == 0) {
					overlayActive = false;
				}
			}
			if (timer != 0) {
				client.player.sendMessage(Text.translatable("use.stopwatch.reset"), false);
			}
			timer = 0;
			timeOld = 0;
			timerActive = false;
		}
		if (overlayActive) {
			long timeCurrent = System.nanoTime();
			if (Stopwatch.stopStart.wasPressed()) {
				if (timerActive) {
					timerActive = false;
					client.player.sendMessage(Text.translatable("use.stopwatch.stop"), false);
					Stopwatch.LOGGER.info("Stopwatch stopped at: " + getTime());
				} else {
					timerActive = true;
					timeStart = System.nanoTime();
					timeCurrent = System.nanoTime();
					timeOld = timer;
					client.player.sendMessage(Text.translatable("use.stopwatch.start"), false);
				}
			}
			if (timerActive) {
				timer = timeOld + (timeCurrent - timeStart);
			}
			String time = getTime();
			TextRenderer textRenderer = this.getTextRenderer();
			int m = textRenderer.getWidth(time);
			context.getMatrices().push();
			context.getMatrices().translate((float) scaledWidth - (m + 5), (float) scaledHeight - 5, 0.0F);
			RenderSystem.enableBlend();
			context.getMatrices().push();
			context.getMatrices().scale(scale, scale, scale);
			//context.drawTextWithBackground(textRenderer, -10, m, 16777215);
			context.drawTextWithShadow(textRenderer, time, -m / 2, -10, 16777215);
			context.getMatrices().pop();
			RenderSystem.disableBlend();
			context.getMatrices().pop();
		}
	}

	@Unique
	private String getTime() {
		long milliseconds = (Math.round(timer/1E6)) % 1000;
		long seconds = (Math.round(timer / 1E9)) % 60;
		long minutes = Math.round(Math.floor(((timer / 1E9)/60) % 60));
		long hours = Math.round(Math.floor((timer / 1E9)/(60*60)));
		String millisecondsString;
		if (milliseconds < 10) {
			millisecondsString = "00" + milliseconds;
		} else if (milliseconds < 100) {
			millisecondsString = "0" + milliseconds;
		} else {
			millisecondsString = String.valueOf(milliseconds);
		}
		return prepString(hours) + ":" + prepString(minutes) + ":" + prepString(seconds) + ":" + millisecondsString;
	}
}
