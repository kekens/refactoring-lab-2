import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length == 1) {
            Client client = new Client(args[0]);
            client.startClient();
        } else {
            System.out.println("Define URL");
        }

    }

}
