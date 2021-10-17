import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Scanner;

public class Client {

    private final String COMMAND_ADD = "ADD";
    private final String COMMAND_SUB = "SUB";
    private final String COMMAND_MUL = "MUL";
    private final String COMMAND_DIV = "DIV";
    private final String COMMAND_MOVE = "#";
    private final String COMMAND_HELP = "HELP";
    private final String COMMAND_QUIT = "Q";

    private final int TYPE_OPERAND = 0;
    private final int TYPE_OPERATION = 1;

    private final String URL;

    // Session
    CloseableHttpClient httpClient = HttpClients.createDefault();
    CookieStore cookieStore = new BasicCookieStore();
    HttpClientContext httpClientContext = HttpClientContext.create();

    public Client(String URL) {
        this.URL = URL;
    }

    public void startClient() throws IOException {
        httpClientContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        httpClient.execute(new HttpGet(URL), httpClientContext);

        // Calculator input
        Scanner scanner = new Scanner(System.in);
        int type = TYPE_OPERAND;
        double prevNumber = Double.NaN;
        String operation = "";
        int step = 0;
        DecimalFormat df = new DecimalFormat("#.####");
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(sym);

        while (true) {

            String URI = "";

            if (type == TYPE_OPERAND) {
                System.out.print("> ");
                String nextline = scanner.nextLine();
                double num;

                try {
                    num = Double.parseDouble(nextline);
                } catch (NumberFormatException e) {
                    System.out.println("Please, enter the number in format [#.###]");
                    continue;
                }

                if (Double.isNaN(prevNumber)) {
                    prevNumber = num;
                } else {
                    URI = String.format("%s/%s?arg1=%s&arg2=%s", URL, operation.toLowerCase(), df.format(prevNumber), df.format(num));
                    String response = sendGetRequest(new HttpGet(URI));

                    try {
                        prevNumber = Double.parseDouble(response);
                    } catch (NumberFormatException e) {
                        System.out.println(response);
                        continue;
                    }
                }

                System.out.printf("[#%d]=%s\n", ++step, df.format(prevNumber));
                type = TYPE_OPERATION;
            } else if (type == TYPE_OPERATION) {
                System.out.print("@: ");
                String nextline = scanner.nextLine().toUpperCase();

                if (nextline.equals(COMMAND_QUIT)) {
                    System.out.println("That's all");
                    URI = String.format("%s?operation=%s", URL, COMMAND_QUIT);
                    sendGetRequest(new HttpGet(URI));
                    return;
                } else if (nextline.equals(COMMAND_HELP)) {
                    URI = String.format("%s/%s", URL, COMMAND_HELP.toLowerCase());
                    System.out.println(sendGetRequest(new HttpGet(URI)));
                } else if (nextline.startsWith(COMMAND_MOVE)) {
                    int stepInput = Integer.parseInt(nextline.substring(1));
                    URI = String.format("%s/%s?step=%d", URL, "step", stepInput);
                    double num;
                    String response = sendGetRequest(new HttpGet(URI));

                    try {
                        num = Double.parseDouble(response);
                    } catch (NumberFormatException e) {
                        System.out.println(response);
                        continue;
                    }

                    System.out.printf("[#%d]=%s\n", ++step, df.format(num));
                } else {

                    switch (nextline) {
                        case "+":
                            operation = COMMAND_ADD;
                            break;
                        case "-":
                            operation = COMMAND_SUB;
                            break;
                        case "*":
                            operation = COMMAND_MUL;
                            break;
                        case "/":
                            operation = COMMAND_DIV;
                            break;
                        default:
                            System.out.println("Operation not found");
                            continue;
                    }

                    type = TYPE_OPERAND;
                }
            }

        }
    }

    private String sendGetRequest(HttpGet httpGet) throws IOException {
        try (CloseableHttpResponse response = httpClient.execute(httpGet, httpClientContext)) {

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                return result;
            }

        }

        return "";
    }


}
