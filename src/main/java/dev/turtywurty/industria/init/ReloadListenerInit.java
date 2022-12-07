package dev.turtywurty.industria.init;

import dev.turtywurty.industria.data.ResearchDataOld;
import io.github.darealturtywurty.turtylib.core.data.SimpleJsonDataManager;

public class ReloadListenerInit {
    public static final SimpleJsonDataManager<ResearchDataOld> RESEARCH_DATA = new SimpleJsonDataManager<>("research_data",
            ResearchDataOld.class);
}
