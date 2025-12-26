import server.Server;

public class ServerApp{
    public static void main(String[] args) {
        System.out.println("Starting Server...");
        Thread server = Server.getInstance();
        server.start();
    }
}
