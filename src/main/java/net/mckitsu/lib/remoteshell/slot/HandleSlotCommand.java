package net.mckitsu.lib.remoteshell.slot;

import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.network.net.NetClientSlotEvent;

public class HandleSlotCommand implements NetClientSlotEvent {
    private final NetClientSlot slot;
    private boolean init = false;

    /* **************************************************************************************
     *  Abstract method
     */

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
        if(!this.init){
            if(new String(netClientSlot.read()).equalsIgnoreCase("command")){
                this.init = true;
           }else{
                netClientSlot.close();
            }
            return;
        }

        while (!netClientSlot.isEmpty())
                netClientSlot.read();
    }

    @Override
    public void onClose(NetClientSlot netClientSlot) {
        System.out.println("SlotClose - Command");
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
}
