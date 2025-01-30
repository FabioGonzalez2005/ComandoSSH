package comandossh;

import com.jcraft.jsch.*;
import java.io.*;

public class ComandoSSH {

    private static final String USERNAME = "alumno";
    private static final String HOST = "localhost";
    private static final int PORT = 22;
    private static final String PASSWORD = "Alumno123";

    public static void main(String[] args) {
        try {
            SSHConnector sshConnector = new SSHConnector();
            sshConnector.connect(USERNAME, PASSWORD, HOST, PORT);
            String output = sshConnector.executeCommand("ls -l");
            System.out.println("Salida del comando:\n" + output);
            sshConnector.disconnect();
        } catch (JSchException | IOException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}

class SSHConnector {
    private Session session;

    public void connect(String username, String password, String host, int port)
            throws JSchException, IllegalAccessException {
        if (this.session == null || !this.session.isConnected()) {
            JSch jsch = new JSch();
            this.session = jsch.getSession(username, host, port);
            this.session.setPassword(password);
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect();
        } else {
            throw new IllegalAccessException("Sesión SSH ya iniciada");
        }
    }

    public String executeCommand(String command) throws JSchException, IOException, IllegalAccessException {
        if (this.session != null && this.session.isConnected()) {
            ChannelExec channelExec = (ChannelExec) this.session.openChannel("exec");
            channelExec.setCommand(command);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);

            InputStream inputStream = channelExec.getInputStream();
            channelExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder outputBuffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuffer.append(line).append("\n");
            }

            channelExec.disconnect();
            return outputBuffer.toString();
        } else {
            throw new IllegalAccessException("No existe sesión SSH iniciada");
        }
    }

    public void disconnect() {
        if (this.session != null && this.session.isConnected()) {
            this.session.disconnect();
        }
    }
}
