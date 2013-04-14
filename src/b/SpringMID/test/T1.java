package b.SpringMID.test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class T1 {
	
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
