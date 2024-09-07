package net.oikmo.engine.save;

import java.io.Serializable;
import java.util.List;

import net.oikmo.engine.network.Server;

public class ServerListData implements Serializable {
	private static final long serialVersionUID = 1L;
	public List<Server> servers;
	
	public ServerListData(List<Server> servers) {
		this.servers = servers;
	}
}
