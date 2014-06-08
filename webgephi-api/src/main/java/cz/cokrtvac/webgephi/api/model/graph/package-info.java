/**
 * User: Vaclav Cokrt, beziks@gmail.com
 * Date: 7. 6. 2014
 * Time: 1:26
 */
@XmlSchema(
        elementFormDefault = XmlNsForm.QUALIFIED,
        namespace = JaxbConstants.WEBGEPHI_NAMESPACE_URL,
        xmlns = {@XmlNs(prefix = JaxbConstants.WEBGEPHI_NAMESPACE_PREFIX, namespaceURI = JaxbConstants.WEBGEPHI_NAMESPACE_URL)}
) package cz.cokrtvac.webgephi.api.model.graph;

import cz.cokrtvac.webgephi.api.model.JaxbConstants;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;