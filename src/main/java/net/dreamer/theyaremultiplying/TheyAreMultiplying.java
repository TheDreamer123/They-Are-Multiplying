package net.dreamer.theyaremultiplying;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheyAreMultiplying implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("They Are Multiplying!");
	public static String MOD_ID = "theyaremultiplying";

	@Override
	public void onInitialize() {
		LOGGER.info("Great... NOW THE PHANTOMS ARE MULTIPLYING-");
	}
}
