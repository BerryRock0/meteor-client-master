/* Compatibility alias for the 26.x mount container menu. */
package net.minecraft.world.inventory;

public abstract class AbstractMountInventoryMenu extends net.minecraft.world.inventory.AbstractContainerMenu {
    protected AbstractMountInventoryMenu(net.minecraft.world.inventory.MenuType<?> type, int containerId,
                                         net.minecraft.world.entity.player.Inventory playerInventory) {
        super(type, containerId);
    }


    public AbstractMountInventoryMenu(net.minecraft.world.inventory.MenuType<?> a0, int a1) {
        super(a0, a1);
    }
}
