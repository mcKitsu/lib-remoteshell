package net.mckitsu.lib.remoteshell.slot;

import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.network.net.NetClientSlotEvent;
import net.mckitsu.lib.terminal.Terminal;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public abstract class HandleSlotTerminal implements NetClientSlotEvent {
    private final NetClientSlot slot;

    /* **************************************************************************************
     *  Abstract method
     */
    protected abstract Logger getLogger();

    protected abstract Terminal getTerminal();

    /* **************************************************************************************
     *  Construct method
     */
    public HandleSlotTerminal(NetClientSlot netClientSlot){
        this.slot = netClientSlot;
        this.slot.event.setEvent(this);
    }

    /* **************************************************************************************
     *  Override method
     */

    @Override
    public void onReceiver(NetClientSlot netClientSlot) {
        while (!netClientSlot.isEmpty())
            this.onRead(netClientSlot.read());

    }

    @Override
    public void onClose(NetClientSlot netClientSlot) {
    }

    /* **************************************************************************************
     *  Public method
     */

    public void send(String log){
        slot.send(log.getBytes(StandardCharsets.UTF_8));
    }

    /* **************************************************************************************
     *  Protected method
     */

    /* **************************************************************************************
     *  Private method
     */
    private void onRead(byte[] data){
        this.getTerminal().executeCommand(new String(data, StandardCharsets.UTF_8));
    }
}
