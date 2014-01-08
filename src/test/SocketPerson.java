package test;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class SocketPerson {

	String host;
	int port ;
	Socket socket;
	char msg;
	private Object writeObject = new Object();
	static int sleepTime;

	public SocketPerson(int port){
		this.port = port;
	}

	public static void main(String[] args) throws UnknownHostException, IOException {

		int port = 8100;
		sleepTime = 1000;
		int threadNum = 1;
		if(args.length > 0){	
			port = Integer.parseInt(args[0]);
			if(args.length > 1){
				threadNum = Integer.parseInt(args[1]);
				if(args.length > 2){
					sleepTime = Integer.parseInt(args[2]);
				}
			}

		}


		for(int i = 0;i < threadNum;i++){
			SocketPerson person = new SocketPerson(port);
			person.host = "127.0.0.1"; 
			person.msg = (char)(1 + 'a');
			person.initSokcet();
			person.readMsgOfRb();
			person.msgUpdate();
			person.writeMsgToRb();
		}

	}

	public void initSokcet() throws UnknownHostException, IOException{	
		socket = new Socket(host,port);	
	}

	public void msgUpdate(){
		Thread msgUpdate = new Thread("msgUpdate"){
			public void run(){
				try { 
					while(true){
						synchronized (writeObject) {
							msg = (char)(Math.random()*26+'a');
							writeObject.notifyAll(); 
						}	
						sleep(sleepTime);				
					}					

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		msgUpdate.start();
	}

	public void writeMsgToRb(){
		Thread writeThread = new Thread("writMsgThread"){
			public void run(){
				try {
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					while(true){
						synchronized (writeObject) {
							writeObject.wait();
							System.out.println("write msg to rb :"+ msg +"  write date" + new Date());
							out.write(msg);
						}					
					}					

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		writeThread.start();
	}

	public void readMsgOfRb(){
		Thread readThread = new Thread("readMsgThread"){
			public void run(){
				try {
					DataInputStream input = new DataInputStream(socket.getInputStream());
					while(true){
						System.out.println("rotbot said :"+input.readChar());
					}					

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		readThread.start();
	}


}
