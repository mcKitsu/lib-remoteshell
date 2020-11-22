package net.mckitsu.lib.remoteshell;

import net.mckitsu.lib.network.net.NetClient;
import net.mckitsu.lib.network.net.NetClientEvent;
import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.remoteshell.slot.SlotCommand;
import net.mckitsu.lib.remoteshell.slot.SlotTerminal;
import net.mckitsu.lib.terminal.TerminalCommand;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class RemoteShell implements NetClientEvent {
    private final Logger logger;
    private final NetClient netClient;
    private SlotCommand slotCommand;
    private SlotTerminal slotTerminal;

    /* **************************************************************************************
     *  Abstract method
     */

    /* **************************************************************************************
     *  Construct method
     */
    public RemoteShell(byte[] verifyKey, Logger logger) throws IOException {
        this.netClient = new NetClient(verifyKey);
        this.netClient.event.setEvent(this);
        this.logger = logger;
    }

    public RemoteShell(byte[] verifyKey) throws IOException {
        this(verifyKey, Logger.getAnonymousLogger());
    }

    /* **************************************************************************************
     *  Override method
     */
    @Override
    public void onDisconnect() {
        this.memoryInit();
    }

    @Override
    public void onRemoteDisconnect() {
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
        synchronized (this){
            this.notify();
        }
    }

    @Override
    public void onAccept(NetClientSlot netClientSlot) {
        if(this.slotCommand == null){
            slotCommand = new SlotCommand(netClientSlot) {
                @Override
                protected Logger getLogger() {
                    return RemoteShell.this.logger;
                }
            };
            return;
        }

        if(this.slotTerminal == null){
            slotTerminal = new SlotTerminal(netClientSlot) {
                @Override
                protected Logger getLogger() {
                    return RemoteShell.this.logger;
                }
            };
            return;
        }

        netClientSlot.close();
    }
    /* **************************************************************************************
     *  Public method
     */

    public boolean connect(InetSocketAddress serverAddress){
        this.netClient.connect(serverAddress, 0);

        synchronized (this){
            try {
                this.wait();
            } catch (InterruptedException e) {
                return false;
            }
        }

        return netClient.isConnect();
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
}
