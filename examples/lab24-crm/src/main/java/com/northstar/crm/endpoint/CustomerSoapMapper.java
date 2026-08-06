package com.northstar.crm.endpoint;

import com.northstar.crm.model.Customer;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

@Component
public class CustomerSoapMapper {
  static final String NAMESPACE = "http://northstar.com/crm/customers";

  public String customerIdFromGetRequest(Element request) {
    NodeList nodes = request.getElementsByTagNameNS(NAMESPACE, "customerId");
    if (nodes.getLength() == 0) {
      throw new IllegalArgumentException("customerId missing in GetCustomerRequest");
    }
    return nodes.item(0).getTextContent().trim();
  }

  public Element toGetCustomerResponse(Customer customer) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      Document doc = factory.newDocumentBuilder().newDocument();
      Element response = doc.createElementNS(NAMESPACE, "GetCustomerResponse");
      response.appendChild(child(doc, "customerId", customer.getId()));
      response.appendChild(child(doc, "name", customer.getName()));
      response.appendChild(child(doc, "email", customer.getEmail()));
      response.appendChild(child(doc, "status", customer.getStatus()));
      return response;
    } catch (Exception e) {
      throw new RuntimeException("Failed to build GetCustomerResponse", e);
    }
  }

  private Element child(Document doc, String name, String value) {
    Element el = doc.createElementNS(NAMESPACE, name);
    el.setTextContent(value == null ? "" : value);
    return el;
  }
}
