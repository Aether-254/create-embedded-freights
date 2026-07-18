package awa.Aether_254.create_embedded_freights.mixin;

import awa.Aether_254.create_embedded_freights.PackageSafety;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PackagerBlockEntity.class)
abstract class PackagerBlockEntityMixin {
    @Redirect(
        method = "attemptToSend(Ljava/util/List;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canFitInsideContainerItems()Z", remap = true),
        remap = false
    )
    private boolean createEmbeddedFreights$allowNestedPackages(Item item) {
        return item instanceof PackageItem || item.canFitInsideContainerItems();
    }

    @Redirect(
        method = "attemptToSend(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/transfer/v1/storage/StorageView;extract(Ljava/lang/Object;JLnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)J",
            remap = false
        ),
        remap = false
    )
    private long createEmbeddedFreights$rejectUnsafePackages(
        StorageView<ItemVariant> view,
        ItemVariant resource,
        long maxAmount,
        TransactionContext transaction
    ) {
        if (!PackageSafety.canPack(resource.toStack(1)))
            return 0;
        return view.extract(resource, maxAmount, transaction);
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
