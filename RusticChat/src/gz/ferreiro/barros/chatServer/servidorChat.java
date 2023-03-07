package gz.ferreiro.barros.chatServer;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import gz.ferriero.barros.chatClient.EnvioPaqueteDatos;

public class servidorChat {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MarcoServidor servidor=new MarcoServidor();
		servidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
}

@SuppressWarnings("serial")
class MarcoServidor extends JFrame {
	
	public MarcoServidor() {
		//Refactorizar!!!!!!!!!! Quitar hilos!!! Ordenar!!! Separar en clases!!!
		outputPort=Integer.parseInt(JOptionPane.showInputDialog("Puerto de salida (ha de coincidir con el de entrada)\nRecomendado: 8080"));
		inputPort=Integer.parseInt(JOptionPane.showInputDialog("Puerto de entrada (ha de coincidir con el de salida)\nRecomendado: 9999"));
		
		setTitle("Servidor");
		setLocation(700,300);
		setSize(400, 500);
		
		JPanel lamina=new JPanel();
		lamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		lamina.add(areatexto, BorderLayout.CENTER);
		
		add(lamina);
		
		setVisible(true);
		
		//Sin usar Threads, aquí iría todo el código de run. Probar.
		
		try {
			@SuppressWarnings("resource")
			ServerSocket miServidor=new ServerSocket(inputPort); 
			EnvioPaqueteDatos paqueteRecibido;	
			
			String nick, ip, mensaje;
			
			HashMap<String, String> listaIp=new HashMap<String, String>(); //para almacenar las ip
			
			
			while(true) {
				
				Socket miSocket=miServidor.accept();
				
			//------VIDEO 2------------------------------------------	
				//Cambios y ponemos que recibimos un Objecto
				ObjectInputStream flujoEntrada=new ObjectInputStream(miSocket.getInputStream());
				  System.out.println("Entrada detectada-----------------------");
					//recibimos el paquete
					paqueteRecibido=(EnvioPaqueteDatos) flujoEntrada.readObject(); //read object puede leer objetos serializados
					
					//Almacenamos la información del objeto
					
					nick=paqueteRecibido.getNick();
					ip=paqueteRecibido.getIp(); //aquí realmente solo necesitamos saber la ip
					mensaje=paqueteRecibido.getTexto();
					
					if(!mensaje.equals("online")) { //Si no se conecta por primera vez
					
					areatexto.append("\nNick: " + nick + " Mensaje: " + mensaje + " IP: " + ip);
			//-------VIDEO 3----------------------------------------
					//Enviar el mesnaje al cliente destino. Los clientes, por la anto, deberán tener un server socket también para estar a la escucha
				
					Socket salida=new Socket(ip, outputPort);
					
					ObjectOutputStream flujoSalida=new ObjectOutputStream(salida.getOutputStream());
					
					flujoSalida.writeObject(paqueteRecibido); //Ahora tenemos que hacer que el cliente pueda recibir esto
			
					salida.close();
				
					miSocket.close();
					
					}
					else { //Si se conecta por primera vez (si el mensaje es online
						
					//Obtenemos la dirección ip de los clientes que se conectan. Podríamos hacerlo directamente:getInetAdress().getHostAdress() 
					//Estas 3 líneas solo deben ejecutarse la primera vez y el resto de veces debe emepzar por el VIDEO 2. If 
					InetAddress dirClientes=miSocket.getInetAddress();
					String ipClientesConectados=dirClientes.getHostAddress();
					
					System.out.println("Cliente conectado: " + ipClientesConectados + " Nick: " + nick);
					
					
					//Necesitamos que el cliente cree un socket nada más iniciarse, no solo al darle a enviar
					listaIp.put(ipClientesConectados, nick); //guardamos las ip y los nicks en el HashMap. Era más fácil si hubiera puesto el nick como key y la ip como valor
					
					paqueteRecibido.setIPs(listaIp); //agregamos el paquete al arraylist
					
						for(Map.Entry<String, String> ips : listaIp.entrySet()) {
						
						
						//En cada vuelta de bucle abro un socket envía la lista a cada dirección ip conectada
						Socket salida=new Socket(ips.getKey(), outputPort); //a esa dirección ip
						
						ObjectOutputStream flujoSalida=new ObjectOutputStream(salida.getOutputStream());
						
						flujoSalida.writeObject(paqueteRecibido); //Ahora tenemos que hacer que el cliente pueda recibir esto
				
						salida.close();
					
						miSocket.close();
						
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) { //Esta nos serive para capturar la d eObjectInputStream
			e.printStackTrace();
		}		
	}
	
	
	private JTextArea areatexto;
	int outputPort;
	int inputPort;
		
}

