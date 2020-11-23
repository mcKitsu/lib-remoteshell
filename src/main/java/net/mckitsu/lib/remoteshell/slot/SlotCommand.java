package net.mckitsu.lib.remoteshell.slot;

import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.network.net.NetClientSlotEvent;

import java.util.logging.Logger;

public abstract class SlotCommand implements NetClientSlotEvent {
    private final NetClientSlot slot;

    /* **************************************************************************************
     *  Abstract method
     */
    protected abstract Logger getLogger();

    protected abstract void onCommand(byte[] command);


    /* **************************************************************************************
     *  Construct method
     */
    public SlotCommand(NetClientSlot netClientSlot, byte[] token){
        this.slot = netClientSlot;
        this.slot.event.setEvent(this);
        this.slot.send(token);
    }

    /* **************************************************************************************
     *  Override method
     */

    @Override
    public void onReceiver(NetClientSlot netClientSlot) {
        while (!netClientSlot.isEmpty())
            onRead(netClientSlot.read());
    }

    @Override
    public void onClose(NetClientSlot netClientSlot) {

    }

    /* **************************************************************************************
     *  Public method
     */

    /* **************************************************************************************
     *  Protected method
     */

    /* **************************************************************************************
     *  Private method
     */
    private void onRead(byte[] data){
        this.onCommand(data);
    }
}
