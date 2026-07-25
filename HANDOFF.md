# Handoff — TechReMix

## Goal
Expand the TechReMix addon for TechReborn (1.20.1) beyond harvest-only handlers to support **mushroom/chorus farming** via a custom **DarkGrowthLampBlock** and a **GreenhouseCeilingKit** upgrade item for the greenhouse controller multiblock.

## Current Progress

### Completed
| Deliverable | Files |
|---|---|
| **DarkGrowthLampBlock** — powered block (1 EU/t), light level 3 when active, 0 when off. Extends `Block` + `EntityBlock`, has a `DarkGrowthLampBlockEntity` extending `PowerAcceptorBlockEntity` + `IToolDrop`. Accepts energy from all sides (33 capacity, 32 input). | `block/DarkGrowthLampBlock.java`, `block/DarkGrowthLampBlockEntity.java` |
| **Block entity registration** — `ModBlocks` registers block, item, BE type via `FabricBlockEntityTypeBuilder`. | `init/ModBlocks.java` |
| **Multiblock integration** — `GreenhouseControllerMixin` @Overwrites `writeMultiblock` to accept either `LampBlock.isActive()` or `DarkGrowthLampBlock.isActive()` at the 9 lamp positions. | `greenhouse/mixin/GreenhouseControllerMixin.java` |
| **Mushroom/chorus growth boost** — (currently in mixin TAIL, might need removal per ceiling upgrade approach) Calls `MushroomBlock.growMushroom()` and `ChorusFlowerBlock.randomTick()` during growth boost when dark lamps are present. | same mixin |
| **Greenhouse plant API** — `GreenhousePlantHandler` interface, `GreenhousePlantRegistry` (CopyOnWriteArrayList), `HarvestOffer` record. | `greenhouse/api/*.java` |
| **Chorus flower harvesting** — `ChorusFlowerHandler` harvests mature (age 5), resets to age 0. | `greenhouse/handler/ChorusFlowerHandler.java` |
| **Huge mushroom harvesting** — `HugeMushroomHandler` harvests any huge mushroom block, destroys it. | `greenhouse/handler/HugeMushroomHandler.java` |
| **Mod initializer** — registers blocks, plant handlers. | `TechReMixMod.java` |
| **Client initializer** — empty, ready for LambDynamicLights or client mixins. | `client/TechReMixModClient.java` |
| **Asset files** — block model (cube_all), item model, blockstate, lang (`"Dark Growth Lamp"`), recipe (lamp + ink + obsidian), loot table (drops self), purple texture (16x16). | `resources/assets/techremix/...` |
| **TechReborn upgrade system** — confirmed greenhouse controller already inherits 4 upgrade slots from `MachineBaseBlockEntity`. Screen builder auto-adds them. Existing upgrades (overclocker, etc.) compile but have no effect. | (research only) |
| **LambDynamicLights integration** — Light level 3 + purple texture = LambDynamicLights auto-samples purple color for placed block glow. No code needed. | `DarkGrowthLampBlock.lightLevel(state -> 3)` |
| **Mixin configs** — `techremix.greenhouse.mixins.json` (greenhouse mixins), `techremix.mixins.json` (empty, for future main mixins), `techremix.client.mixins.json` (empty, for future client mixins). | `resources/` |

### Active / Pending
- **GreenhouseCeilingKit upgrade** — design done, not yet implemented. Each kit in an upgrade slot raises lamp y from `3` to `3 + count * 4`, giving room for huge mushrooms (5+ stem) and chorus plants. Recipe suggested in Next Steps.
- **`lambdynlights:initializer` entrypoint** — not yet implemented. Optional dependency for colored dynamic light on the held/dropped item.

### What Worked
- TechReborn 5.8.15 is frozen; @Overwrite of `writeMultiblock` is safe and simple.
- The `MachineBaseBlockEntity` upgrade inventory with 4 slots is inherited automatically — no GUI changes needed.
- `IUpgrade` interface is a simple lambda `(MachineBaseBlockEntity, IUpgradeHandler, ItemStack) -> void`.
- Mojang mappings + cross-mapping: `LampBlock` and `DarkGrowthLampBlock` coexist cleanly in the same predicate.

### What Didn't Work
- Method reference `DarkGrowthLampBlockEntity::tick` for `getTicker` causes generic type mismatch — must use explicit lambda with cast.
- `MushroomBlock.grow()` in Yarn → Mojang is `growMushroom()` with different parameter order `(ServerLevel, BlockPos, BlockState, RandomSource)`.
- `@Shadow` field `world` → `level` in Mojang unmapped — verified via `javap` on the merged jar.

### What to Avoid / Known Footguns
- **Greenhouse mixins → `techremix.greenhouse.mixins.json`** (package `com.techremix.greenhouse.mixin`). Use empty `techremix.mixins.json` (package `com.techremix.mixin`) for non-greenhouse main mixins. Use `techremix.client.mixins.json` (package `com.techremix.client.mixin`) for client-only mixins.
- **Do NOT overwrite `AGENTS.md`** — it was deleted per user request.
- **Loom `1.17-SNAPSHOT`** — `loom.officialMojangMappings()` is active. All imports use Mojang packages.
- **Mixin AP is configured** (`annotationProcessor "net.fabricmc:fabric-loader"` in build.gradle). `@Shadow` method bodies are auto-generated; the explicit `throw new AbstractMethodError("Shadow")` pattern in existing code is valid but optional.
- **`requireAnnotations: true`** is set for all mixin configs — `@Overwrite` annotation is required.
- **TechReborn + RebornCore are `modImplementation` dependencies** from CurseMaven, NOT JarJar'd. Both must be declared for compile classpath.
- **LambDynamicLights 4.4.0+1.20.1** uses `lambdynlights:initializer` entrypoint (`DynamicLightsInitializer` interface) for registering item light sources. Block color is auto-sampled from texture. API lives in `dev.lambdaurora.lambdynlights.api.*`. Client-side integration goes in `techremix.client.mixins.json`.
- **ScreenBuilder** auto-detects `IUpgradeable` and adds upgrade slots at x=-18 via `BlockEntityScreenHandlerBuilder` constructor. No manual upgrade slot code needed.

## Next Steps

1. **Implement `GreenhouseCeilingKit`** item extending `UpgradeItem` → marker `IUpgrade`. Register in `src/main/java/com/techremix/init/ModItems.java`.
2. **Update mixin** — add `getCeilingUpgradeCount()` scanning `getUpgradeInventory()`, modify `writeMultiblock` lamp y from `3` to `3 + count * 4`.
3. **Remove mushroom/chorus growth boost from mixin TAIL** (recommended) — the taller ceiling under dark lamps makes `randomTick` on chorus and `growMushroom()` on small mushrooms viable naturally. The explicit TAIL injection becomes redundant.
4. **Recipe for ceiling kit** — suggest: `[glass_pane, dark_growth_lamp, glass_pane; chorus_fruit, ender_pearl, chorus_fruit; glass_pane, dark_growth_lamp, glass_pane]` in TechReborn shaped recipe style.
5. **Build** — `./gradlew build` (before in-game testing to catch compile errors).
6. **Optional: `lambdynlights:initializer` entrypoint** — add to fabric.mod.json, register block item as `ItemLightSource` via `DynamicLightsInitializer`. Use `techremix.client.mixins.json` for any client mixins.
7. **Test in-game** — place controller, dark lamps, upgrade slots, soil, mushrooms/chorus.

## Suggested Skills
- `confidence-check` — pre-implementation confidence for ceiling kit
- `code-review-skill` — review mixin changes for correctness
- `banner-design` / `design` — if creating promotional images
