@XmlSchema(attributeFormDefault = XmlNsForm.QUALIFIED,
        elementFormDefault = XmlNsForm.QUALIFIED,
        namespace = "http://sftpclient.ak.ru/",
        xmlns = {
                @XmlNs(namespaceURI = "http://sftpclient.ak.ru/", prefix = ""),
                @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi"),
                @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema", prefix = "xs")})
package ru.ak.info;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;