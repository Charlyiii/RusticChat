package gz.ferriero.barros.chatClient;

import java.io.Serializable;
import java.util.HashMap;

public class EnvioPaqueteDatos implements Serializable {
	private static final long serialVersionUID = 1L;

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public HashMap<String, String> getIPs() {
		return IPs;
	}

	public void setIPs(HashMap<String, String> iPs) {
		IPs = iPs;
	}


	private String nick, ip, texto;
	private HashMap<String, String> IPs; //para recibir la lista de IPs conectados
}
