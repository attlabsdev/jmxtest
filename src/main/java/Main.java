import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Set;
import org.apache.cassandra.metrics.CassandraMetricsRegistry;

public class Main {

    public static void main(String[] args) throws IOException, MalformedObjectNameException {

        JMXServiceURL url = new JMXServiceURL(
                "service:jmx:rmi:///jndi/rmi://[144.60.88.170]:7199/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        Set<ObjectInstance> objs = mbsc.queryMBeans(ObjectName
                .getInstance("org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency"), null);
        for (ObjectInstance obj : objs) {
            Object proxy = JMX.newMBeanProxy(mbsc, obj.getObjectName(),
                    CassandraMetricsRegistry.JmxMeterMBean.class);
            if (proxy instanceof CassandraMetricsRegistry.JmxMeterMBean) {
                System.out.println("Read Rate (1 min): " + ((CassandraMetricsRegistry.JmxMeterMBean) proxy).getOneMinuteRate());
            }
        }
        jmxc.close();


    }

}
