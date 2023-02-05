package dev.turtywurty.industria.client;

import dev.turtywurty.industria.registry.ResearchData;
import net.minecraft.client.model.Model;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProjectorModels {
    private static final Map<ResearchData, Model> MODELS = new HashMap<>();
    private static boolean LOCKED = true;

    public static void registerModel(RegistryObject<ResearchData> research, Model model) {
        registerModel(research.get(), model);
    }

    public static void registerModel(ResearchData research, Model model) {
        if (LOCKED) throw new IllegalStateException("Cannot register models after the game has started!");
        if (MODELS.put(research, model) != null) throw new IllegalStateException("Duplicate model registration!");
    }

    public static Optional<Model> getModel(ResearchData research) {
        return MODELS.entrySet().stream().filter(entry -> entry.getKey().equals(research)).findFirst()
                     .map(Map.Entry::getValue);
    }

    public static void lock() {
        if (LOCKED) throw new IllegalStateException("Models has already been locked!");
        LOCKED = true;
    }

    public static void unlock() {
        if (!LOCKED) throw new IllegalStateException("Models has already been unlocked!");
        LOCKED = false;
    }
}