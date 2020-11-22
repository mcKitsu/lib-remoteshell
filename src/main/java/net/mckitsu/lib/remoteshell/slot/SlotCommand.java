package net.mckitsu.lib.remoteshell.slot;

import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.network.net.NetClientSlotEvent;

import java.util.logging.Logger;

public abstract class SlotCommand implements NetClientSlotEvent {
    private final NetClientSlot slot;
    private boolean init = false;

    /* **************************************************************************************
     *  Abstract method
     */
    protected abstract Logger getLogger();


    /* **************************************************************************************
     *  Construct method
     */
    public SlotCommand(NetClientSlot netClientSlot){
        this.slot = netClientSlot;
        this.slot.event.setEvent(this);
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
        if(!this.init){
            if(new String(data).equalsIgnoreCase("command")){
                this.init = true;
                slot.send("command".getBytes());
            }else{
                slot.close();
            }
        }

    }
}
