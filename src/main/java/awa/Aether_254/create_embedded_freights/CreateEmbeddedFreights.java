package awa.Aether_254.create_embedded_freights;

import awa.Aether_254.create_embedded_freights.client.EmbeddedFreightsModMenu;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("create_embedded_freights")
public final class CreateEmbeddedFreights {
    public CreateEmbeddedFreights(ModContainer container) {
        EmbeddedFreightsConfig.load();
        if (FMLEnvironment.dist == Dist.CLIENT)
            EmbeddedFreightsModMenu.register(container);
    }
}
