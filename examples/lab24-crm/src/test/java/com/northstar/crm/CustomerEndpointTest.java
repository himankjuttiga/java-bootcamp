package com.northstar.crm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;
import java.util.Map;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

@SpringBootTest
class CustomerEndpointTest {

  @Autowired
  ApplicationContext applicationContext;

  private MockWebServiceClient client;

  @BeforeEach
  void setup() {
    client = MockWebServiceClient.createClient(applicationContext);
  }

  @Test
  void getCustomerReturnsCus1001() {
    Source request = new StringSource(
        "<GetCustomerRequest xmlns=\"http://northstar.com/crm/customers\">"
            + "<customerId>CUS-1001</customerId>"
            + "</GetCustomerRequest>");

    Map<String, String> ns = Map.of("tns", "http://northstar.com/crm/customers");

    client.sendRequest(withPayload(request))
        .andExpect(noFault())
        .andExpect(xpath("//tns:name", ns).evaluatesTo("Amina Khan"))
        .andExpect(xpath("//tns:status", ns).evaluatesTo("ACTIVE"));
  }
}
