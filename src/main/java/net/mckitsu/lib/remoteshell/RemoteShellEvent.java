package net.mckitsu.lib.remoteshell;

import java.util.function.Consumer;

public interface RemoteShellEvent {
    void onCommand(byte[] command);

    void onConnect(RemoteShell remoteShell);

    void onDisconnect(RemoteShell remoteShell);
}
