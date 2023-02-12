package dev.turtywurty.industria.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;

public class FakeClientPlayer extends RemotePlayer {
    public FakeClientPlayer(ClientLevel pClientLevel, GameProfile pGameProfile) {
        super(pClientLevel, pGameProfile, null);
    }
}