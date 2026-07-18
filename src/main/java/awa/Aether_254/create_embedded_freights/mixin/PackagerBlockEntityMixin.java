package awa.Aether_254.create_embedded_freights.mixin;

import awa.Aether_254.create_embedded_freights.PackageSafety;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PackagerBlockEntity.class)
abstract class PackagerBlockEntityMixin {
    @Redirect(
        method = "attemptToSend(Ljava/util/List;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canFitInsideContainerItems()Z"),
        remap = false
    )
    private boolean createEmbeddedFreights$allowNestedPackages(Item item) {
        return item instanceof PackageItem || item.canFitInsideContainerItems();
    }

    @Redirect(
        method = "attemptToSend(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/items/IItemHandler;extractItem(IIZ)Lnet/minecraft/world/item/ItemStack;"
        ),
        remap = false
    )
    private ItemStack createEmbeddedFreights$rejectUnsafePackages(
        IItemHandler inventory,
        int slot,
        int amount,
        boolean simulate
    ) {
        ItemStack extracted = inventory.extractItem(slot, amount, simulate);
        PackagerBlockEntity self = (PackagerBlockEntity) (Object) this;
        return PackageSafety.canPack(extracted, self.getLevel().registryAccess()) ? extracted : ItemStack.EMPTY;
    }

    @ModifyExpressionValue(
        method = "attemptToSend(Ljava/util/List;)V",
        at = @At(value = "INSTANCEOF", target = "Lcom/simibubi/create/content/logistics/box/PackageItem;"),
        remap = false
    )
    private boolean createEmbeddedFreights$alwaysWrapExtractedPackages(boolean original) {
        return false;
    }
}
