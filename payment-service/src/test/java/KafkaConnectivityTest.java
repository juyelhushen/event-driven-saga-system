import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KafkaConnectivityTest {
    public static void main(String[] args) {
        // --- 1. SET YOUR UBUNTU IP HERE ---
        String bootstrapServer = "172.22.36.65:9092";
        System.out.println("Connecting to Kafka at: " + bootstrapServer);

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        // We set a short timeout so we don't wait forever if it's blocked
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "5000");

        try (AdminClient client = AdminClient.create(props)) {
            System.out.println("Checking metadata...");
            ListTopicsResult topics = client.listTopics();
            Set<String> names = topics.names().get(5, TimeUnit.SECONDS);

            System.out.println("SUCCESS! Connected to Kafka.");
            System.out.println("Topics found: " + names);

        } catch (Exception e) {
            System.err.println("CONNECTION FAILED!");
            System.err.println("Reason: " + e.getMessage());
            System.err.println("\nChecklist:");
            System.err.println("1. Is Ubuntu Kafka running?");
            System.err.println("2. Is advertised.listeners set to the IP in server.properties?");
            System.err.println("3. Is the Windows Firewall blocking Java/IntelliJ?");
        }
    }
}