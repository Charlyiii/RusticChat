package gz.ferriero.barros.chatClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LaminaCliente extends JPanel {

	@SuppressWarnings({ "rawtypes"})
	public LaminaCliente(String ipServer, int inputPort, int outputPort) {
		
		this.inputPort = inputPort;
			
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
					
					System.out.println("Hilo de envío: " + Thread.currentThread().getName());
					
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
	
	 int inputPort;
	 JTextField texto;
	 JButton boton;
	//añadimos un area de texto para también poder reecibir mensajes
	 JTextArea area;
	//private JTextField nick;
	 JLabel nick;
	//private JTextField ip;
	//Lo cambiamos por un JComboBox, quedaría mejor con un JList
	@SuppressWarnings("rawtypes")
	 JComboBox usuario;
	HashMap<String, String> IpsCombo;
}
