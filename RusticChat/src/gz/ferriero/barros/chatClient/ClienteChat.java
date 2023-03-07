package gz.ferriero.barros.chatClient;


import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;



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
		
		@SuppressWarnings("unused")
		Escuchador listen = new Escuchador(miLamina);
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

