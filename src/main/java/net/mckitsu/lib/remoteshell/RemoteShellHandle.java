package net.mckitsu.lib.remoteshell;

import net.mckitsu.lib.network.net.NetClient;
import net.mckitsu.lib.network.net.NetClientEvent;
import net.mckitsu.lib.network.net.NetClientSlot;
import net.mckitsu.lib.remoteshell.slot.HandleSlotCommand;
import net.mckitsu.lib.remoteshell.slot.HandleSlotTerminal;
import net.mckitsu.lib.terminal.Terminal;
import net.mckitsu.lib.terminal.TerminalCommand;

import java.util.Map;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;

public abstract class RemoteShellHandle implements NetClientEvent{
    private final Terminal terminal;
    private final Handler handler;
    private final HandleSlotCommand slotCommand;
    private final HandleSlotTerminal slotTerminal;
    private final NetClient netClient;
    private final Logger terminalLogger;

    /* **************************************************************************************
     *  Abstract method
     */
    protected abstract Map<String, TerminalCommand> getCommands();

    protected abstract Logger getLogger();

    /* **************************************************************************************
     *  Construct method
     */
    public RemoteShellHandle(NetClient netClient){
        this.terminalLogger = Logger.getAnonymousLogger();
        this.terminalLogger.setUseParentHandlers(false);

        this.handler = constructHandle();
        this.terminalLogger.addHandler(handler);

        this.terminal = constructTerminal(getCommands(), terminalLogger);

        this.netClient = netClient;
        this.netClient.event.setEvent(this);

        this.slotCommand = new HandleSlotCommand(netClient.openSlot());
        this.slotTerminal = new HandleSlotTerminal(netClient.openSlot()) {
            @Override
            protected Logger getLogger() {
                return RemoteShellHandle.this.terminalLogger;
            }

            @Override
            protected Terminal getTerminal() {
                return RemoteShellHandle.this.terminal;
            }
        };

        this.getLogger().info("RemoteShell: connect from " + netClient.getRemoteAddress());
    }

    /* **************************************************************************************
     *  Override method
     */

    @Override
    public void onDisconnect() {
        this.terminalLogger.removeHandler(this.handler);
        this.handler.close();
    }

    @Override
    public void onRemoteDisconnect() {
        this.terminalLogger.removeHandler(this.handler);
        this.handler.close();
    }

    @Override
    public void onConnectFail() {

    }

    @Override
    public void onConnect(NetClient netClient) {

    }

    @Override
    public void onAccept(NetClientSlot netClientSlot) {

    }

    /* **************************************************************************************
     *  Public method
     */

    /* **************************************************************************************
     *  protected method
     */

    /* **************************************************************************************
     *  Private method
     */
    private Terminal constructTerminal(Map<String, TerminalCommand> commands, Logger logger){
        return new Terminal(commands, logger) {
            @Override
            protected boolean onStart() {
                return true;
            }

            @Override
            protected String onRead() {
                return "";
            }

            @Override
            protected void onUnknownCommand(String[] strings) {

            }
        };
    }

    private Handler constructHandle(){
        return new Handler(){
            @Override
            public void publish(LogRecord record) {
                slotTerminal.send(record.getLevel() + ";" + record.getMessage());
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        };
    }
}
