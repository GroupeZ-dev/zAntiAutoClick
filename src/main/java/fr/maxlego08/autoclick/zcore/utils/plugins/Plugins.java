package fr.maxlego08.autoclick.zcore.utils.plugins;

public enum Plugins {
	
	VAULT("Vault"),
	ESSENTIALS("Essentials"),
	HEADDATABASE("HeadDatabase"), 
	ZHEAD("zHead"),
	PLACEHOLDER("PlaceholderAPI"),
	CITIZENS("Citizens"),
	TRANSLATIONAPI("TranslationAPI"),
	ZTRANSLATOR("zTranslator"),
	ORAXEN("Oraxen"),
	ITEMSADDER("ItemsAdder"),
	SLIMEFUN("Slimefun"),
	NOVA("Nova"),
	ECO("eco"),
	ZITEMS("zItems"),
	HMCCOSMETICS("HMCCosmetics"),
	JOBS("Jobs"),
	LUCKPERMS("LuckPerms"),
    NEXO("Nexo"),
	MAGICCOSMETICS("MagicCosmetics");

	private final String name;

	Plugins(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
