package ui;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import server.BackupServer;
import utils.Debug;

public class BasicInterface {

	public static final String MC_ADDRESS = "239.0.0.1";
	public static final int MC_PORT = 8765;
	public static final String MDB_ADDRESS = "239.0.0.1";
	public static final int MDB_PORT = 8766;
	public static final String MDR_ADDRESS = "239.0.0.1";
	public static final int MDR_PORT = 8767;

	public static void main(String args[]) {
		BackupServer bck = initServerWithArguments(args);
		bck.start();
		bck.backupFile(new File("./data/randomfile.rf"), 0);
	}

	static BackupServer initServerWithArguments(String[] args) {
		String mc_address, mdb_address, mdr_address;
		int mc_port, mdb_port, mdr_port;

		if (checkArguments(args)) {
			mc_address = args[0];
			mc_port = Integer.parseInt(args[1]);
			mdb_address = args[2];
			mdb_port = Integer.parseInt(args[3]);
			mdr_address = args[4];
			mdr_port = Integer.parseInt(args[5]);
			if (Debug.on)
				System.out
						.println("Using addresses and ports received as arguments");
		} else {
			mc_address = MC_ADDRESS;
			mc_port = MC_PORT;
			mdb_address = MDB_ADDRESS;
			mdb_port = MDB_PORT;
			mdr_address = MDR_ADDRESS;
			mdr_port = MDR_PORT;

			if (Debug.on)
				System.out.println("Using default addresses and ports");
		}

		BackupServer bck = new BackupServer(mc_address, mc_port, mdb_address,
				mdb_port, mdr_address, mdr_port);
		return bck;
	}

	public static boolean checkArguments(String[] args) {

		// 6 arguments in cmd
		if (args.length != 6)
			return false;

		try {
			Integer.parseInt(args[1]);
			Integer.parseInt(args[3]);
			Integer.parseInt(args[5]);
			if (!(InetAddress.getByName(args[0]).isMulticastAddress()
					| InetAddress.getByName(args[2]).isMulticastAddress() | InetAddress
					.getByName(args[4]).isMulticastAddress()))
				return false;

		} catch (NumberFormatException | UnknownHostException e) {
			return false;
		}

		return true;
	}
}
