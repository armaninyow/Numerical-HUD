package com.armaninyow.numericalhud.hud.modules;

import com.armaninyow.numericalhud.mixin.BossHealthOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.Collections;

public class BossBarModule extends BaseHudModule {

	private static final int COLOR_WHITE = 0xFFFFFFFF;
	private static final int COLOR_RED   = 0xFFFF0000;
	private static final int ROW_HEIGHT  = 19;

	// Background sprites indexed by BossBarColor ordinal
	private static final Identifier[] BG_SPRITES = {
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/pink_background"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/blue_background"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/red_background"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/green_background"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/yellow_background"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/purple_background"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/white_background"),
	};

	// Progress sprites indexed by BossBarColor ordinal
	private static final Identifier[] FG_SPRITES = {
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/pink_progress"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/blue_progress"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/red_progress"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/green_progress"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/yellow_progress"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/purple_progress"),
		Identifier.fromNamespaceAndPath("minecraft", "boss_bar/white_progress"),
	};

	private final Map<UUID, BossState> bossStates = new LinkedHashMap<>();

	@Override
	protected IconRenderer getIconRenderer() {
		return VersionIconRenderer.INSTANCE;
	}

	@Override
	public void render(GuiGraphicsExtractor context, Player player, int x, int y, float tickDelta) {
		Minecraft client = Minecraft.getInstance();
		if (client.gui == null || client.level == null) return;

		Map<UUID, LerpingBossEvent> events =
			((BossHealthOverlayAccessor) client.gui.getBossOverlay()).getEvents();

		if (events.isEmpty()) return;

		// Scan client entities for actual HP values, grouped by name
		Map<String, List<Float>> entityHealthByName = new HashMap<>();
		for (Entity entity : client.level.entitiesForRendering()) {
			if (entity instanceof LivingEntity living) {
				entityHealthByName
					.computeIfAbsent(living.getName().getString(), k -> new ArrayList<>())
					.add(living.getHealth());
			}
		}
		// Track how many times each name has been consumed this frame
		Map<String, Integer> nameIndex = new HashMap<>();

		List<UUID> ids = new ArrayList<>(events.keySet());
		bossStates.keySet().retainAll(ids);

		int screenWidth = context.guiWidth();

		for (int i = 0; i < ids.size(); i++) {
			UUID id = ids.get(i);
			LerpingBossEvent event = events.get(id);
			float progress = event.getProgress();

			// Pick sprites by color
			int colorIdx = Math.min(event.getColor().ordinal(), BG_SPRITES.length - 1);
			Identifier bgSprite = BG_SPRITES[colorIdx];
			Identifier fgSprite = FG_SPRITES[colorIdx];

			// Actual HP if available — match by name, consuming one entry per boss event
			String bossName = event.getName().getString();
			List<Float> healthList = entityHealthByName.getOrDefault(bossName, Collections.emptyList());
			int idx = nameIndex.getOrDefault(bossName, 0);
			Float actualHealth = idx < healthList.size() ? healthList.get(idx) : null;
			nameIndex.put(bossName, idx + 1);

			BossState state = bossStates.computeIfAbsent(id, k -> new BossState());

			String text = null;
			if (actualHealth != null) {
				text = getStyledText(actualHealth);
			} else if (state.isFillingUp(progress)) {
				text = Math.round(progress * 100f) + "%";
			}

			float displayHealth = actualHealth != null ? actualHealth : progress * 100f;
			int tickCount = Minecraft.getInstance().gui.getGuiTicks();
			state.triggerBlink(displayHealth, tickCount);
			state.update(displayHealth);

			// Center icon + optional text horizontally
			int textWidth = text != null ? client.font.width(text) : 0;
			int totalWidth = text != null ? ICON_SIZE + ICON_TEXT_GAP + textWidth : ICON_SIZE;
			int moduleX = screenWidth / 2 - totalWidth / 2;
			int barY = 12 + i * ROW_HEIGHT;

			// Background bar slice always visible
			drawVanillaBossBar(context, bgSprite, moduleX, barY + 2);

			// Progress bar slice — hidden during blink frames
			if (progress > 0f && !state.shouldBlink(tickCount)) {
				drawVanillaBossBar(context, fgSprite, moduleX, barY + 2);
			}

			// Text — only shown when applicable
			if (text != null) {
				int color = (actualHealth != null && progress < 0.2f) ? COLOR_RED : COLOR_WHITE;
				drawText(context, text, moduleX + ICON_SIZE + ICON_TEXT_GAP, barY + 1, color);
			}
		}
	}

	private static class BossState {
		float lastHealth = Float.MAX_VALUE;
		private float lastProgress = -1f;
		private boolean fillingUp = false;
		private long blinkTime = 0;

		void update(float health) {
			lastHealth = health;
		}

		boolean shouldBlink(int tickCount) {
			return blinkTime > tickCount
				&& ((blinkTime - tickCount) / 3) % 2 == 1;
		}

		void triggerBlink(float health, int tickCount) {
			if (lastHealth != Float.MAX_VALUE) {
				if (health < lastHealth) {
					blinkTime = tickCount + 20;
				} else if (health > lastHealth) {
					blinkTime = tickCount + 10;
				}
			}
		}

		boolean isFillingUp(float progress) {
			boolean result = fillingUp;
			if (lastProgress < 0f) {
				fillingUp = progress < 1.0f;
			} else if (progress > lastProgress) {
				fillingUp = true;
			} else if (progress >= 1.0f || progress < lastProgress) {
				fillingUp = false;
			}
			lastProgress = progress;
			return result;
		}
	}
}