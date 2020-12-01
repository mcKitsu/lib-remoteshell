package net.mckitsu.lib.remoteshell;

import lombok.Getter;
import lombok.Setter;
import net.mckitsu.lib.network.net.NetClient;
import net.mckitsu.lib.network.net.NetServer;
import net.mckitsu.lib.terminal.TerminalCommand;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class RemoteShellServer extends NetServer {
    private final Map<byte[], UserToken> tokens = new HashMap<byte[], UserToken>();

    @Setter @Getter
    private String name = "RemoteShellServer";

    /* **************************************************************************************
     *  Abstract method
     */
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
            protected boolean onVerifyToken(byte[] token) {
                return RemoteShellServer.this.onClientVerifyToken(token, this);
            }
        };
    }

    /* **************************************************************************************
     *  Public method
     */
    public void addToken(String username, byte[] token, Map<String, TerminalCommand> commands){
        this.tokens.put(token, new UserToken(username, commands));
    }

    /* **************************************************************************************
     *  protected method
     */

    /* **************************************************************************************
     *  Private method
     */
    private boolean onClientVerifyToken(byte[] token, RemoteShellHandle handle){
        UserToken userToken = RemoteShellServer.this.tokens.get(token);
        if(userToken == null) {
            getLogger().info(String.format("[%s] Verify token fail from \"%s\".", this.name, handle.getRemoteAddress()));
            return false;
        }

        getLogger().info(String.format("[%s] User \" %s\" is login.", this.name, handle.getRemoteAddress()));
        handle.setCommands(userToken.commands);
        return true;
    }


    /* **************************************************************************************
     *  Class UserToken
     */
    private static class UserToken{
        public final String username;
        public final Map<String, TerminalCommand> commands;

        public UserToken(String username, Map<String, TerminalCommand> commands){
            this.username = username;
            this.commands = commands;
        }
    }
}
