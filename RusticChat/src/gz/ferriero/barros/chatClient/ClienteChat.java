package gz.ferriero.barros.chatClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class ClienteChat {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MarcoCliente cliente=new MarcoCliente();
		cliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

@SuppressWarnings("serial")
class MarcoCliente extends JFrame{
	
	public MarcoCliente() {
		
		String ipServer=JOptionPane.showInputDialog("Introduce la ip del servidor: ");
		int outputPort=Integer.parseInt(JOptionPane.showInputDialog("Puerto de salida (ha de coincidir con el de entrada del servidor)\nRecomendado: 9999"));
		int inputPort=Integer.parseInt(JOptionPane.showInputDialog("Puerto de entrada (ha de coincidir con el de salida del servidor)\nRecomendado: 8080"));
		
		setTitle("Cliente");
		setLocation(700,300);
		setSize(500, 500);
		LaminaCliente miLamina=new LaminaCliente(ipServer, inputPort, outputPort);
		add(miLamina);
		setVisible(true);
		
		
		//Abrimos aquí el socket para que el servidor detecte la conecxión nad amás abrirse
		estableceConexionServidor(miLamina, ipServer, outputPort);
				
	}
	
	//método para la conexión
	public void estableceConexionServidor(LaminaCliente lamina, String ipServer, int outputPort) {
		
		try {
			@SuppressWarnings("resource")
			Socket miSocket=new Socket(ipServer, outputPort);
			
			EnvioPaqueteDatos datos=new EnvioPaqueteDatos();
			
			datos.setTexto("online"); //indicamos que sería la primera conexión
			datos.setNick(lamina.nick.getText());
			
			ObjectOutputStream flujo=new ObjectOutputStream(miSocket.getOutputStream());
			
			flujo.writeObject(datos);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Hay que pulirlo!!!!!!
			System.out.println("No se puede establecer conexión con el servidor");
		}
		
	}
}

@SuppressWarnings("serial")
class LaminaCliente extends JPanel implements Runnable { 
	
	@SuppressWarnings({ "rawtypes"})
	public LaminaCliente(String ipServer, int inputPort, int outputPort) {
		
		this.inputPort = inputPort;
		
		
		
		//afregamos casilla para el nick
		//nick=new JTextField(9);
		//add(nick);
		//Lo cambiamos por un JLabel para que no se pueda modificar después de iniciar el char
		//Preguntamos el nombre al iniciar		
		nick=new JLabel(JOptionPane.showInputDialog("Introduce tu nick")); //Él lo hace con setText, es lo mismo
		add(nick);
	
		add(new JLabel(" <------     Online->"));
		
		//para poner la dirección IP del destinatario (cambiamos a nombre)
		usuario=new JComboBox();
		//Tenemos que cargar la ip dinamicamente, lo haremos con ArrayList
		add(usuario);
		
		area=new JTextArea(23, 37);
		add(area); //la ponemos arriba
		
		texto=new JTextField(37);
		texto.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent e) { //sobreescribimos el método
				
				if(e.getKeyCode()==KeyEvent.VK_ENTER) boton.doClick(); //Si se pulsa enter, es como clicar el botón
			}
			
		}); //añadimos el KeyListener a la barra 
		
		add(texto);
		
		boton=new JButton("Enviar");
		
		boton.addActionListener(new ActionListener() { //Creo el evento de pulsado del botón
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
		//--------------VIDEO 1 --------------------------------------------
				//Creamos una clase para almacenar toda la información	
					
					Socket miSocket=new Socket(ipServer, outputPort); //ip sobremesa				
					//Creamos un objeto paquete para almacenar y enviar todos los datos
					EnvioPaqueteDatos datos=new EnvioPaqueteDatos();
					
					//damos valor a los datos y almacenamos en las propiedades del objeto
					datos.setNick(nick.getText());
					//datos.setIp(ip.getText());
					if(usuario.getSelectedItem() != null) {
					datos.setIp(obtenerKeyDeValor(IpsCombo, usuario.getSelectedItem().toString())); //devuelve object, hay que pasarlo a String
					}
					else area.append("\n-----El siguiente mensaje no se pudo enviar:");
					//Tenemos que conseguir que el cliente le mande una señal al servidor de que está conectado y que el servidor avise al cliente quien está conectado
					datos.setTexto(texto.getText());
					//quedaría listo. Ahora habría que enviarlo al servidor
					area.append("\n" + nick.getText() + ": " + texto.getText());
					texto.setText("");
					
		//----------VIDEO 2-------------------------------------------------			
			//Enviamos el paquete con ObjetStream, hay que serializarlo 
					
					ObjectOutputStream flujo=new ObjectOutputStream(miSocket.getOutputStream());
					flujo.writeObject(datos);
					
					flujo.close();
					
					miSocket.close();
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		add(boton);
		
		//Sin usar Threads aquí iría todo el código de run
		
		//ejecutamos runnable
		
		Thread miHilo=new Thread(this);
		
		miHilo.start();
		
		//Debemos hacer que esté a la escucha también, Runnable
		
	}

	@SuppressWarnings({ "unchecked", "resource" })
	@Override
	public void run() { //seguramente funcione sin hilos como la otra
		// TODO Auto-generated method stub
		try {
			
			ServerSocket miServidor=new ServerSocket(inputPort); //ojo puerto diferente de entrada. Coincide con el de salida del server
			
			Socket socketEntrada;
			
			EnvioPaqueteDatos paqueteRecibido;
			
			while(true) {
				
				
				socketEntrada=miServidor.accept();
				
				//-----------------------cuando se conecte un cliente, se podría mandar la ip al servidor, para que le diga a los demás que está
				//System.out.println(InetAddress.getLocalHost().getHostAddress()); con esto obtenemos la ip 
				
				ObjectInputStream flujoEntrada=new ObjectInputStream(socketEntrada.getInputStream());
				
				paqueteRecibido=(EnvioPaqueteDatos) flujoEntrada.readObject();
				
				if(paqueteRecibido.getTexto().equals("online")) { //cuando se conecte por primera vez
								
					//aquí extraemos la lista y rellenar el JCombo
				IpsCombo=new HashMap<String, String>();
				IpsCombo=paqueteRecibido.getIPs();
				
				usuario.removeAllItems(); //vaciamos el combo para que no se repita
				
				for (Map.Entry<String, String> ips : IpsCombo.entrySet()) {
					
					if(!ips.getKey().equals(InetAddress.getLocalHost().getHostAddress())) { //para que no salga nuestra ip
					usuario.addItem(ips.getValue()); //añadimos el nombre del hashmap
					}
				}
					
				}else {
				
				
				area.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getTexto());
				
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
	//Método para poder obtener la clave (IP) mediante el valor (nombre)
	//Si hubiera puesto la ip como valor no tendría que hacer todo esto. Me lleagaba con el método get(ObjectKey).
	public static <K, V> K obtenerKeyDeValor(Map<K, V> map, V valor) { //podría ser no static
		
		for (Map.Entry<K, V> entry : map.entrySet()) {
			
			if(Objects.equals(valor, entry.getValue())) { //método equals de Objects, compara 2 objetos
				
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	private int inputPort;
	private JTextField texto;
	private JButton boton;
	//añadimos un area de texto para también poder reecibir mensajes
	private JTextArea area;
	//private JTextField nick;
	protected JLabel nick;
	//private JTextField ip;
	//Lo cambiamos por un JComboBox, quedaría mejor con un JList
	@SuppressWarnings("rawtypes")
	private JComboBox usuario;
	HashMap<String, String> IpsCombo;
}

