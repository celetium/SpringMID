package b.SpringMID.core;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.zeromq.ZMQ;

public class T1 {

	class T1Peer implements Runnable {
		private ZMQ.Context context;
		T1Peer(ZMQ.Context context) {
			this.context = context;
		}
		@Override
		public void run() {
			ZMQ.Socket socket = context.socket(ZMQ.PUSH);
			socket.connect("ipc:///tmp/a");
			ZMQ.Socket socket2 = context.socket(ZMQ.PULL);
			socket2.connect("ipc:///tmp/a");
			byte[] b = "1234".getBytes();
			socket.send(b, 0);
			byte[] b1 = socket2.recv(0);
			System.out.println("Received mine: " + b1);
		}
	}
	
	void start() {
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.PULL);
		socket.bind("ipc:///tmp/a");
		new Thread(new T1Peer(context)).start();
		ZMQ.Socket socket2 = context.socket(ZMQ.PUSH);
		socket2.connect("ipc:///tmp/a");
		while (true) {
			byte[] b = socket.recv(0);
			System.out.println("Received & return");
			socket2.send(b, 0);
		}
	}
	
	void start2() {
		System.out.println("domain: " + System.getProperty("domainId"));
		Enumeration<NetworkInterface> eni;
		try {
			eni = NetworkInterface.getNetworkInterfaces();
			while (eni.hasMoreElements()) {
				NetworkInterface ni = eni.nextElement();
				System.out.println("NI: " + ni.getName());
				Enumeration<InetAddress> eia = ni.getInetAddresses();
				while (eia.hasMoreElements()) {
					InetAddress ia = eia.nextElement();
					if (ia instanceof Inet4Address)
						System.out.println("IA: " + ia.getHostName() + "/" + ia.getHostAddress());
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		T1 t = new T1();
		t.start2();
	}

}
