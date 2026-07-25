# TechReUtility Tester's Guide

This checklist details the scenarios and verification steps to validate that all features in **TechReUtility** function correctly in-game.

---

## 1. Mod Setup & Registry
- **Custom Creative Tab**:
  - Open the creative inventory search or tabs.
  - Locate the **TechReUtility** tab.
  - **Verify**: The tab's icon is the *Greenhouse Ceiling Upgrade* item.
  - **Verify**: The tab contains exactly two items: *Dark Growth Lamp* and *Greenhouse Ceiling Upgrade*.
- **Item Tooltips & Localizations**:
  - Hover over the items in the tab.
  - **Verify**: The name tags show `"Dark Growth Lamp"` and `"Ceiling Upgrade"` (no missing translation keys like `item.trutils...`).

---

## 2. Dark Growth Lamp Mechanics
- **Energy Interaction**:
  - Connect a generator or power cable (up to Medium Voltage, max 32 EU/t input) to the lamp.
  - **Verify**: The lamp accepts power from any of its 6 sides.
  - **Verify**: When powered, the lamp changes its state to active (active texture glows purple).
- **Lighting & Glow**:
  - Measure the light level on/around the active lamp.
  - **Verify**: Light level is exactly `3` when active, and `0` when powered off.
  - **Verify**: (If *LambDynamicLights* is installed) Held/dropped item glows purple dynamically.

---

## 3. Multiblock Validation & Ceiling Height
- **Greenhouse Multiblock Verification**:
  - Build a standard TechReborn Greenhouse multiblock.
  - Replace the 9 standard incandescent lamps at the ceiling with *Dark Growth Lamps*.
  - **Verify**: The Greenhouse Controller accepts them as valid lamps, and the multiblock structure remains active.
- **Upgrade Height Capping**:
  - **Default**: With no upgrades in the controller, verify it checks lamps at exactly y = `3`.
  - **Upgrades**: Insert upgrade items into the controller's upgrade slot.
    - Insert 1 upgrade $\rightarrow$ Verify scanning height is `4`.
    - Insert 2 upgrades $\rightarrow$ Verify scanning height is `5`.
    - Insert 3+ upgrades $\rightarrow$ Verify scanning height is capped at `6`.

---

## 4. Harvest Handlers (Farming)
- **Chorus Fruit Farming**:
  - Plant chorus seeds under the greenhouse.
  - Let the chorus plant grow to maturity (flower reaches age 5).
  - **Verify**: The controller harvests the mature flower, resets it to age 0 (allowing it to grow again), and transfers the harvested items into the controller's inventory.
- **Huge Mushroom Farming**:
  - Grow a huge red or brown mushroom within the greenhouse range.
  - **Verify**: The greenhouse controller destroys the huge mushroom blocks, harvests them, and transfers drops to inventory.
- **BetterNether: Magma Flower**:
  - Plant a magma flower on a magma block inside the greenhouse.
  - Let it grow to its maximum age stage (`3`).
  - **Verify**: The greenhouse harvests the flower, resets the block's age state back to `0`, and gathers the drops.
- **BetterNether: Golden Vine**:
  - Hang a golden vine from the ceiling inside the greenhouse.
  - Let it grow downwards.
  - **Verify**: The greenhouse harvests the bottom-most segments of the vine (where `bottom` is true).
  - **Verify**: The top-most (root) block is **never** harvested, allowing the vine to grow back dynamically.
