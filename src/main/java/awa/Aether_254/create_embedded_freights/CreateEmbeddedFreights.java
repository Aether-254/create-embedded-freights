package awa.Aether_254.create_embedded_freights;

import awa.Aether_254.create_embedded_freights.client.EmbeddedFreightsModMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod("create_embedded_freights")
public final class CreateEmbeddedFreights {
    public CreateEmbeddedFreights() {
        EmbeddedFreightsConfig.load();
        if (FMLLoader.getDist() == Dist.CLIENT)
            EmbeddedFreightsModMenu.register();
    }
}
