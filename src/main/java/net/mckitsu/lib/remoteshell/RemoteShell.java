package net.mckitsu.lib.remoteshell;

import lombok.Setter;
import net.mckitsu.lib.network.net.EventHandler;
import net.mckitsu.lib.network.net.NetClient;
import net.mckitsu.lib.network.net.NetClientEvent;
import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.remoteshell.slot.SlotCommand;
import net.mckitsu.lib.remoteshell.slot.SlotTerminal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.logging.Logger;


public class RemoteShell implements NetClientEvent {
    public final Event event = new Event();

    private final Logger logger;
    private final NetClient netClient;
    private SlotCommand slotCommand;
    private SlotTerminal slotTerminal;
    private final byte[] token;

    /* **************************************************************************************
     *  Abstract method
     */

    /* **************************************************************************************
     *  Construct method
     */
    public RemoteShell(byte[] verifyKey, Logger logger, byte[] token) throws IOException {
        this.netClient = new NetClient(verifyKey);
        this.netClient.event.setEvent(this);
        this.logger = logger;
        this.token = token;
    }

    public RemoteShell(byte[] verifyKey, byte[] token) throws IOException {
        this(verifyKey, Logger.getAnonymousLogger(), token);
    }

    /* **************************************************************************************
     *  Override method
     */
    @Override
    public void onDisconnect() {
        this.event.onDisconnect(this);
        this.memoryInit();
    }

    @Override
    public void onRemoteDisconnect() {
        this.event.onDisconnect(this);
        this.memoryInit();
    }

    @Override
    public void onConnectFail() {
        synchronized (this){
            this.notify();
        }
    }

    @Override
    public void onConnect(NetClient netClient) {
        this.event.onConnect(this);
        synchronized (this){
            this.notify();
        }
    }

    @Override
    public void onAccept(NetClientSlot netClientSlot) {
        if(this.slotCommand == null){
            this.slotCommand = constructSlotCommand(netClientSlot, this.token);
            return;
        }

        if(this.slotTerminal == null){
            this.slotTerminal = constructSlotTerminal(netClientSlot);
            return;
        }

        netClientSlot.close();
    }
    /* **************************************************************************************
     *  Public method
     */

    public boolean connect(InetSocketAddress serverAddress){
        if(this.netClient.isConnect())
            return false;

        this.netClient.connect(serverAddress, 0);

        //blocking wait
        synchronized (this){
            try {
                this.wait();
            } catch (InterruptedException e) {
                return false;
            }
        }

        return this.netClient.isConnect();
    }

    public boolean asyncConnect(InetSocketAddress serverAddress){
        if(this.netClient.isConnect())
            return false;

        this.netClient.connect(serverAddress, 0);
        return true;
    }

    public boolean disconnect(){
        return this.netClient.disconnect();
    }

    public void send(String input){
        this.slotTerminal.send(input);
    }

    /* **************************************************************************************
     *  protected method
     */

    /* **************************************************************************************
     *  Private method
     */

    private void memoryInit(){
        this.slotCommand = null;
        this.slotTerminal = null;
    }

    private SlotCommand constructSlotCommand(NetClientSlot netClientSlot, byte[] token){
        return new SlotCommand(netClientSlot, token) {
            @Override
            protected Logger getLogger() {
                return RemoteShell.this.logger;
            }

            @Override
            protected void onCommand(byte[] command) {
                RemoteShell.this.event.onCommand(command);
            }
        };
    }

    private SlotTerminal constructSlotTerminal(NetClientSlot netClientSlot){
        return new SlotTerminal(netClientSlot) {
            @Override
            protected Logger getLogger() {
                return RemoteShell.this.logger;
            }
        };
    }

    /* **************************************************************************************
     *  Class Event
     */

    @Setter
    public static class Event extends EventHandler{
        private Consumer<byte[]> onCommand;
        private Consumer<RemoteShell> onConnect;
        private Consumer<RemoteShell> onDisconnect;

        protected void onCommand(byte[] command){
            super.execute(this.onCommand ,command);
        }

        protected void onConnect(RemoteShell remoteShell){
            super.execute(onConnect, remoteShell);
        }

        protected void onDisconnect(RemoteShell remoteShell){
            super.execute(onDisconnect, remoteShell);
        }

        public void setEvent(RemoteShellEvent event){
            this.onCommand = event::onCommand;
            this.onConnect = event::onConnect;
            this.onDisconnect = event::onDisconnect;
        }
    }
}
