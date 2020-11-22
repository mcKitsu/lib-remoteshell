package net.mckitsu.lib.remoteshell;

import net.mckitsu.lib.network.net.NetClient;
import net.mckitsu.lib.network.net.NetServer;
import net.mckitsu.lib.terminal.TerminalCommand;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.logging.Logger;

public abstract class RemoteShellServer extends NetServer {
    /* **************************************************************************************
     *  Abstract method
     */

    protected abstract Map<String, TerminalCommand> getCommands();
    protected abstract Logger getLogger();

    /* **************************************************************************************
     *  Construct method
     */

    public RemoteShellServer(byte[] verifyKey, InetSocketAddress address) {
        super(verifyKey);
        super.start(address);
    }

    /* **************************************************************************************
     *  Override method
     */
    @Override
    protected void onAccept(NetClient netClient) {
        new RemoteShellHandle(netClient) {
            @Override
            protected Map<String, TerminalCommand> getCommands() {
                return RemoteShellServer.this.getCommands();
            }

            @Override
            protected Logger getLogger() {
                return RemoteShellServer.this.getLogger();
            }
        };
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
}
