package xyz.derkades.serverselectorx.placeholders;

import java.util.ArrayList;
import java.util.List;

import xyz.derkades.serverselectorx.Main;

public class Server {

	private static final List<Server> SERVERS = new ArrayList<>();

	private final String name;
	private transient long lastInfoTime = 0;
	private List<Placeholder> placeholders;

	public Server(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public long getTimeSinceLastMessage() {
		return System.currentTimeMillis() - this.lastInfoTime;
	}

	public boolean isOnline() {
		final long timeout = Main.getConfigurationManager().getGlobalConfig().getLong("server-offline-timeout", 6000);
		return getTimeSinceLastMessage() < timeout;
	}

	public List<Placeholder> getPlaceholders() {
		return this.placeholders;
	}

	public Placeholder getPlaceholder(final String name) {
		if (getPlaceholders() != null) {
			for (final Placeholder placeholder : getPlaceholders()) {
				if (placeholder.getKey().equals(name))
					return placeholder;
			}
		}

		Main.getPlugin().getLogger().warning("Placeholder " + name + " was requested but not received from the server (" + getName() + ").");
		return new GlobalPlaceholder(name, "0");
	}

	public int getOnlinePlayers() {
		return Integer.parseInt(((GlobalPlaceholder) getPlaceholder("online")).getValue());
	}

	public void updatePlaceholders(final List<Placeholder> placeholders) {
		this.lastInfoTime = System.currentTimeMillis();
		this.placeholders = placeholders;
	}

	public static List<Server> getServers() {
		return SERVERS;
	}

	public static Server getServer(final String name) {
		for (final Server server : SERVERS) {
			if (server.getName().equalsIgnoreCase(name))
				return server;
		}

		final Server server = new Server(name);
		SERVERS.add(server);
		return server;
	}

}