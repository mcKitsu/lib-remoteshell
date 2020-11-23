package net.mckitsu.lib.remoteshell.slot;

import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.network.net.NetClientSlotEvent;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public abstract class SlotTerminal implements NetClientSlotEvent {
    private final NetClientSlot slot;

    protected abstract Logger getLogger();

    /* **************************************************************************************
     *  Construct method
     */

    public SlotTerminal(NetClientSlot netClientSlot){
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

    public void send(String input){
        this.slot.send(input.getBytes(StandardCharsets.UTF_8));
    }

    /* **************************************************************************************
     *  Protected method
     */

    /* **************************************************************************************
     *  Private method
     */

    private void logShow(String log){
        try{
            String type = log.substring(0, log.indexOf(';'));
            String message = log.substring(log.indexOf(';')+1);

            if ("INFO".equalsIgnoreCase(type)) {
                getLogger().info(message);
            }else if("WARNING".equalsIgnoreCase(type)){
                getLogger().warning(message);
            }else if("SEVERE".equalsIgnoreCase(type)){
                getLogger().severe(message);
            }else if("FINEST".equalsIgnoreCase(type)){
                getLogger().finest(message);
            }else if("FINER".equalsIgnoreCase(type)){
                getLogger().finer(message);
            }else if("FINE".equalsIgnoreCase(type)){
                getLogger().fine(message);
            }else if("CONFIG".equalsIgnoreCase(type)){
                getLogger().config(message);
            }

        }catch (IndexOutOfBoundsException ignore){}
    }

    private void onRead(byte[] data){
        this.logShow(new String(data, StandardCharsets.UTF_8));
    }
}
