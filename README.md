[![Buy Me A Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-ffdd00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://www.youtube.com/watch?v=xvFZjo5PgG0)

# Numerical HUD
<details>
  <summary></summary>
   
A comprehensive mod that replaces traditional graphical icon bars with precise, animated numerical modules.
</details>

![Mod Icon](src/main/resources/assets/numericalhud/icon.png)

## Functionality
<details>
  <summary></summary>
   
Numerical HUD overhauls your survival interface by condensing vital stats into a centralized, data-driven display located directly above the experience bar.

### Core Modules
* **Advanced Health & Absorption:** Displays exact HP with color-coded states for Poison, Wither, Frozen, and Hardcore modes. Includes a "Panic" animation when health is low.
* **Smart Armor Tracking:** Real-time numerical defense rating that "blinks" when your armor value changes. Can be hidden entirely when no armor is worn.
* **Detailed Hunger & Saturation:** Tracks food levels and saturation simultaneously, featuring a "starvation" shake animation when hunger hits zero.
* **Precise Experience:** Shows your level and exact progress percentage (e.g., `25.40`) with color transitions from yellow to green as you gain XP.
* **Oxygen & Breath:** A specialized module that only appears underwater, featuring a unique "popping bubble" animation when air is consumed.
* **Vehicle & Mount Stats:** Automatically displays the health of horses, llamas, and other rideable entities when mounted.

### Animation System
Numerical HUD features a configurable **BaseHudModule** animation system with three styles to choose from:
* **Decimal** *(default)*: Values count up/down with decimal precision, turning Green when increasing and Red when decreasing.
* **Fade**: The number snaps instantly while the text color fades from Green/Red back to White over 1 second using a quint ease-in curve.
* **Popup**: A delta label (e.g. `+2`, `-3`) floats beside the module for 1 second, traveling upward for heals and downward for damage with an ease-out effect.
</details>

## Benefits
<details>
  <summary></summary>
   
* **Data-Driven Gameplay:** Know exactly how many HP you have left or how much saturation a food item provided.
* **Clean UI:** Removes the clutter of 40+ individual heart/hunger icons, freeing up screen real estate.
* **Performance:** Built on highly optimized Mixins that stop vanilla bars from rendering entirely, reducing overhead.
* **Context Aware:** Modules for Oxygen and Vehicles only appear when relevant, keeping your HUD minimal during normal play.
* **Configurable:** Tailor the animation style and module visibility to your preference without editing any files.
</details>

## Installation
<details>
  <summary></summary>

### Prerequisites
* **Minecraft:** 1.21.10
* **Loader:** [Fabric Loader](https://fabricmc.net/use/installer/) (>=0.18.4)
* **Core Dependencies:**
    * [Fabric API](https://modrinth.com/mod/fabric-api)
    * [Cloth Config API](https://modrinth.com/mod/cloth-config) (Required for configuration)
    * [Mod Menu](https://modrinth.com/mod/modmenu) (Recommended for configuration)

### Steps
1. Download the latest `.jar` from [Modrinth](https://modrinth.com/mod/numerical-hud) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/numerical-hud).
2. Move the file into your Minecraft `%appdata%/.minecraft/mods` folder.
3. Launch the game using the Fabric profile.
</details>

## Support
<details>
  <summary></summary>
   
If you encounter bugs or wish to contribute:
* [Report any problems you find.](https://github.com/armaninyow/Numerical-HUD/discussions/categories/issues)
* [Share your ideas for new features.](https://github.com/armaninyow/Numerical-HUD/discussions/categories/suggestions)
</details>

## Credits
<details>
  <summary></summary>
   
* **Author**: Armaninyow
* **License**: Released under [CC0-1.0](https://creativecommons.org/publicdomain/zero/1.0/).
</details>

[![Buy Me A Coffee](https://img.shields.io/badge/Buy%20Me%20a%20Coffee-ffdd00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://www.youtube.com/watch?v=xvFZjo5PgG0)
