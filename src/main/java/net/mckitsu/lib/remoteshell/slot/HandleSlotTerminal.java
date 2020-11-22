package net.mckitsu.lib.remoteshell.slot;

import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.network.net.NetClientSlotEvent;
import net.mckitsu.lib.terminal.Terminal;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public abstract class HandleSlotTerminal implements NetClientSlotEvent {
    private final NetClientSlot slot;
    private boolean init = false;

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
        this.slot.send("terminal".getBytes());
    }

    /* **************************************************************************************
     *  Override method
     */

    @Override
    public void onReceiver(NetClientSlot netClientSlot) {
        if(!this.init){
            if(new String(netClientSlot.read()).equalsIgnoreCase("terminal")){
                this.init = true;
            }else{
                netClientSlot.close();
            }
            return;
        }

        while (!netClientSlot.isEmpty())
            this.getTerminal().executeCommand(new String(netClientSlot.read(), StandardCharsets.UTF_8));
    }

    @Override
    public void onClose(NetClientSlot netClientSlot) {
        System.out.println("SlotClose - terminal");
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
}
