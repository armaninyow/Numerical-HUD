# Numerical HUD

A Minecraft mod that replaces the vanilla health, hunger, armor, and XP bars with a clean, numerical display featuring smooth animations and visual feedback.

## Features

### Core Modules

- **Health Display** - Shows current health with support for absorption hearts, status effects (poison, wither, frozen), and hardcore mode
- **Armor Display** - Displays armor points with visual feedback when armor changes
- **Hunger Display** - Shows food level with saturation overlay and hunger effect support
- **XP Display** - Presents experience as level.percentage (e.g., "15.47") with animated progression
- **Oxygen Display** - Appears when underwater or air depleting, with unique bubble pop/push animations
- **Vehicle Health** - Shows mounted entity health when riding

### Visual Animations

**Smooth Value Transitions**
- Health, armor, and hunger values animate smoothly over 10 ticks when changing
- Values display with one decimal place during transitions (e.g., "19.3" → "20.0")
- Separate animations for increasing (green) and decreasing (red) values

**Special Effects**
- **Panic Animation** - Health icon bounces when HP ≤ 4
- **Starvation Animation** - Hunger icon bounces when food level = 0
- **Blink Effect** - Icons flash briefly when taking damage or armor changes
- **Oxygen Burst** - Unique bubble burst animation when losing air
- **XP Glow** - Experience orb glows yellow during XP gain

### Smart Behavior

- All modules positioned relative to XP bar for perfect alignment
- Oxygen module only appears when underwater or air depleting
- Vehicle health shows only when mounted
- Infinity symbol (∞) for water breathing effects
- Red text for critical health/hunger states
- Decimal animations prevent jarring number jumps

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/)
2. Install [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. Download the latest release from [Releases](../../releases)
4. Place the `.jar` file in your `.minecraft/mods` folder

## Technical Details

### Module System

Each HUD element is a self-contained module extending `BaseHudModule`:
- Independent rendering and animation logic
- Shared animation timing and color systems
- Modular texture system for easy customization

### Animation System

- 10-tick linear interpolation for value changes
- 5-tick cooldown between animations to prevent flickering
- Smart decimal display (e.g., increasing: 7 → 7.9 → 8; decreasing: 8 → 7.1 → 7)
- Color-coded feedback (red for damage, green for healing/gain)

### Vanilla Integration

Uses Mixin to:
- Hide vanilla health/armor/hunger/mount hearts
- Temporarily hide XP level number during vanilla rendering
- Preserve vanilla XP bar and hotbar functionality

## Configuration

Currently, the mod works out of the box with no configuration needed. Future versions may include customization options for:
- Module positions
- Animation speeds
- Color schemes
- Toggle individual modules

## Compatibility

**Requires:**
- Minecraft 1.21+
- Fabric Loader
- Fabric API

**May conflict with:**
- Other HUD mods that modify health/hunger/XP rendering
- Mods that heavily customize the player HUD

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

- Textures based on vanilla Minecraft UI elements
- Created using Fabric API and Mixin

## Support

Found a bug or have a suggestion? Please [open an issue](../../issues)!

---

**Note:** This mod is a client-side visual enhancement and does not affect gameplay mechanics or server compatibility.
