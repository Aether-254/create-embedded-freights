package awa.Aether_254.create_embedded_freights;

import com.simibubi.create.content.logistics.box.PackageItem;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import java.util.ArrayDeque;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public final class PackageSafety {
    private PackageSafety() {
    }

    public static boolean canPack(ItemStack stack) {
        EmbeddedFreightsConfig.Data config = EmbeddedFreightsConfig.get();
        if (config.packageDepthLimitEnabled && exceedsPackageDepth(stack, config.maxPackageDepth))
            return false;
        return !config.nbtDepthLimitEnabled || !exceedsNbtDepth(stack, config.maxNbtDepth);
    }

    private static boolean exceedsPackageDepth(ItemStack stack, int limit) {
        if (!(stack.getItem() instanceof PackageItem))
            return 1 > limit;

        ArrayDeque<PackageNode> pending = new ArrayDeque<>();
        pending.push(new PackageNode(stack, 1));

        while (!pending.isEmpty()) {
            PackageNode node = pending.pop();
            if (node.depth() + 1 > limit)
                return true;

            ItemStackHandler contents = PackageItem.getContents(node.stack());
            for (int slot = 0; slot < contents.getSlotCount(); slot++) {
                ItemStack nested = contents.getStackInSlot(slot);
                if (nested.getItem() instanceof PackageItem)
                    pending.push(new PackageNode(nested, node.depth() + 1));
            }
        }
        return false;
    }

    private static boolean exceedsNbtDepth(ItemStack stack, int limit) {
        try {
            ItemStack outerPackage = PackageItem.containing(List.of(stack.copy()));
            return tagDepthExceeds(outerPackage.save(new CompoundTag()), limit);
        } catch (RuntimeException | StackOverflowError ignored) {
            return true;
        }
    }

    private static boolean tagDepthExceeds(Tag root, int limit) {
        ArrayDeque<TagNode> pending = new ArrayDeque<>();
        pending.push(new TagNode(root, 1));

        while (!pending.isEmpty()) {
            TagNode node = pending.pop();
            if (node.depth() > limit)
                return true;

            if (node.tag() instanceof CompoundTag compound) {
                for (String key : compound.getAllKeys()) {
                    Tag child = compound.get(key);
                    if (child != null)
                        pending.push(new TagNode(child, node.depth() + 1));
                }
            } else if (node.tag() instanceof ListTag list) {
                for (Tag child : list)
                    pending.push(new TagNode(child, node.depth() + 1));
            }
        }
        return false;
    }

    private record PackageNode(ItemStack stack, int depth) {
    }

    private record TagNode(Tag tag, int depth) {
    }
}
