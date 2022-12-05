package dev.turtywurty.industria.init;

import dev.turtywurty.industria.data.ResearchData;
import io.github.darealturtywurty.turtylib.core.data.SimpleJsonDataManager;

public class ReloadListenerInit {
    public static final SimpleJsonDataManager<ResearchData> RESEARCH_DATA = new SimpleJsonDataManager<>("research_data",
            ResearchData.class);
}
