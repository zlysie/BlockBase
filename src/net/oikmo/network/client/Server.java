package net.oikmo.network.client;

import java.io.Serializable;

public class Server implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private String name;
	
	public Server(String name, String ip) {
		this.ip = ip;
		this.name = name;
	}

	public String getIP() {
		return ip;
	}

	public String getName() {
		return name;
	}
}
