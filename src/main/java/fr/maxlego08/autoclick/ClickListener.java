package fr.maxlego08.autoclick;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

public class ClickListener implements PacketListener {

    private final ClickPlugin plugin;
    private final int minimumDelay;
    long lastClick = 0;

    public ClickListener(ClickPlugin plugin) {
        this.plugin = plugin;
        this.minimumDelay = plugin.getConfig().getInt("minimum-delay");
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) {
            return;
        }

        var wrapper = new WrapperPlayClientInteractEntity(event);
        if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;

        long now = System.currentTimeMillis();
        if (lastClick != 0) {
            long time = now - lastClick;

            // Si le click est assez lent pour être log
            if (time >= this.minimumDelay) {
                System.out.println("La différence entre les deux clicks : " + time);
            }
        }
        lastClick = now;
    }
}
