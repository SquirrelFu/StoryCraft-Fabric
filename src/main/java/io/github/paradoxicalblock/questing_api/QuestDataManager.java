package io.github.paradoxicalblock.questing_api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.github.paradoxicalblock.questing_api.api.Quest;
import io.github.paradoxicalblock.storycraft.main.StoryCraft;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.mcft.copy.wearables.WearablesCommon;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class QuestDataManager implements SimpleResourceReloadListener<QuestDataManager.RawData> {

    @Override
    public CompletableFuture<RawData> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> new RawData(manager), executor);
    }

    @Override
    public CompletableFuture<Void> apply(RawData data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(data::apply, executor);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(StoryCraft.MOD_ID, "quest_data_manager");
    }

    public void registerReloadListener() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(this);
    }

    public static class RawData {

        private static final Gson GSON = new GsonBuilder()
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
//                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer2())
                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
                .setLenient()
                .create();

        List<Quest> quests = new ArrayList<>();

        RawData(ResourceManager manager) {
            for (Identifier id : manager.findResources("quests", path -> path.endsWith(".json"))) {
                try {
                    InputStreamReader reader = new InputStreamReader(manager.getResource(id).getInputStream());
                    quests.add(GSON.fromJson(reader, Quest.class));
                } catch (JsonIOException | JsonSyntaxException ex) {
                    WearablesCommon.LOGGER.error("[StoryCraft:QuestDataManager] Error while parsing resource '{}'", id, ex);
                } catch (IOException ex) {
                    WearablesCommon.LOGGER.error("[StoryCraft:QuestDataManager] Error reading resource '{}'", id, ex);
                } catch (Exception ex) {
                    WearablesCommon.LOGGER.error("[StoryCraft:QuestDataManager] Error loading resource '{}'", id, ex);
                }
            }
        }

        public void apply() {
            for(Quest quest : this.quests) {
                QuestManager.registerQuests(quest);
                System.out.println(String.format("Registered a quest called %s for the profession %s", quest.getRegistryName(), quest.getProfession()));
            }
        }

    }

}
