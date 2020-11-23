package net.mckitsu.lib.remoteshell.slot;

import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.network.net.NetClientSlotEvent;

public abstract class HandleSlotCommand implements NetClientSlotEvent {
    private final NetClientSlot slot;
    private boolean init = false;

    /* **************************************************************************************
     *  Abstract method
     */

    protected abstract void onVerifyToken(byte[] token);

    protected abstract void onCommand(byte[] command);

    /* **************************************************************************************
     *  Construct method
     */
    public HandleSlotCommand(NetClientSlot netClientSlot){
        this.slot = netClientSlot;
        this.slot.event.setEvent(this);
        this.slot.send("command".getBytes());
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
        public void send(byte[] command){
            this.slot.send(command);
        }

    /* **************************************************************************************
     *  Protected method
     */

    /* **************************************************************************************
     *  Private method
     */
    private void onRead(byte[] data){
        if(!this.init){
            this.init = true;
            onVerifyToken(data);
        }
    }
}
