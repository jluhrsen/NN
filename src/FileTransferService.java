/**
 * Created by jluhrsen on 12/2/16.
 */


import com.jcraft.jsch.*;

public class FileTransferService {

    private JSch jsch = null;
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp c = null;
    private String username = "ec2-user";
    private String host = "www.luhrsenlounge.net";
    private String khfile = "/home/jluhrsen/.ssh/known_hosts";
    private String identityfile = "/home/jluhrsen/jamo/keys/llnetkeypair.pem";
    private String destFolder = "/data/autonets/";

    public void transferFile(String localPath, String localFilename, String destPath, String destFilename) {

        try {
            jsch = new JSch();
            session = jsch.getSession(username, host, 22);
            jsch.setKnownHosts(khfile);
            jsch.addIdentity(identityfile);
            session.connect();

            channel = session.openChannel("sftp");
            channel.connect();
            c = (ChannelSftp) channel;

        } catch (Exception e) { 	e.printStackTrace();	}

        try {
            System.out.println("Sending File: " + localPath + localFilename);
            c.put(localPath + localFilename, destPath + destFilename);

        } catch (Exception e) {	e.printStackTrace();	}

        c.disconnect();
        session.disconnect();

    }
}