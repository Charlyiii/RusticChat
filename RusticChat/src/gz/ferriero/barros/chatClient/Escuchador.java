package gz.ferriero.barros.chatClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Escuchador {

	@SuppressWarnings("unchecked")
	public Escuchador(LaminaCliente miLamina) {
		
try {
			
			System.out.println("Dentro del run");
			
			@SuppressWarnings("resource")
			ServerSocket miServidor=new ServerSocket(miLamina.inputPort); //ojo puerto diferente de entrada. Coincide con el de salida del server
			
			Socket socketEntrada;
			
			EnvioPaqueteDatos paqueteRecibido;
			
			while(true) {
				
				System.out.println("Hilo ejecutandose = A la esperaaaa");
				socketEntrada=miServidor.accept();
				System.out.println("Entrada aceptada");
				
				//-----------------------cuando se conecte un cliente, se podría mandar la ip al servidor, para que le diga a los demás que está
				
				ObjectInputStream flujoEntrada=new ObjectInputStream(socketEntrada.getInputStream());
				
				paqueteRecibido=(EnvioPaqueteDatos) flujoEntrada.readObject();
				
				if(paqueteRecibido.getTexto().equals("online")) { //cuando se conecte por primera vez
								
					//aquí extraemos la lista y rellenar el JCombo
				miLamina.IpsCombo=new HashMap<String, String>();
				miLamina.IpsCombo=paqueteRecibido.getIPs();
				
				miLamina.usuario.removeAllItems(); //vaciamos el combo para que no se repita
				
				for (Map.Entry<String, String> ips : miLamina.IpsCombo.entrySet()) {
					
					if(!ips.getKey().equals(InetAddress.getLocalHost().getHostAddress())) { //para que no salga nuestra ip
					miLamina.usuario.addItem(ips.getValue()); //añadimos el nombre del hashmap
					}
				}
					
				}else {
				
				miLamina.area.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getTexto());
				
				}
				
				//flujoEntrada.close();
				
				//miServidor.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
