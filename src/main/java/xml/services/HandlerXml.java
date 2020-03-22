package xml.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import xml.model.Item;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class HandlerXml {
    private static final Logger LOG = LogManager.getLogger(HandlerXml.class.getName());
    private static final int NUMBER_OF_EMPTY_TAG = 1;
    private static final String ATTRIBUTE_BOX_ID = "id";
    private static final String ATTRIBUTE_ITEM_ID = "id";
    private static final String ATTRIBUTE_ITEM_COLOR = "color";
    private static final String TAG_NAME = "Item";
    private static final int START_NUMBER_FOR_LOOP = 0;
    private static final String NAME_OF_PARENT_TAG = "Box";
    private final Set<Item> uniqueListOfItem;

    public HandlerXml() {
        uniqueListOfItem = new HashSet<>();
    }

    public Set<Item> getUniqueListOfItem() {
        return uniqueListOfItem;
    }

    public Map<Integer, List<Item>> parseXml(String filePath) throws IOException {
        Map<Integer, List<Item>> result = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            NodeList boxElements = document.getDocumentElement().getElementsByTagName(NAME_OF_PARENT_TAG);
            result = getGroupsItemWithParentId(boxElements);
        } catch (ParserConfigurationException | SAXException ps) {
            LOG.error("Смотри в парсер xml файла", ps);
        }
        return result;
    }

    private Map<Integer, List<Item>> getGroupsItemWithParentId(NodeList boxNodeList) {
        Map<Integer, List<Item>> result = new HashMap<>();
        for (int indexNodeBox = START_NUMBER_FOR_LOOP; indexNodeBox < boxNodeList.getLength(); indexNodeBox++) {
            Node box = boxNodeList.item(indexNodeBox);
            NamedNodeMap boxAttributes = box.getAttributes();
            if (box.getChildNodes().getLength() > NUMBER_OF_EMPTY_TAG) {
                if (boxAttributes.getNamedItem(ATTRIBUTE_BOX_ID) != null) {
                    int idParent = Integer.parseInt(boxAttributes.getNamedItem(ATTRIBUTE_BOX_ID).getNodeValue());
                    Queue<Node> queueNode = new LinkedList<>();
                    queueNode.offer(box);
                    result.put(idParent, getListItems(queueNode));
                }
            }
        }
        return result;
    }

    private List<Item> getListItems(Queue<Node> nodeQueue) {
        List<Item> roleList = new ArrayList<>();
        while (!nodeQueue.isEmpty()) {
            Node currentNode = nodeQueue.poll();
            NodeList itemNodes = currentNode.getChildNodes();
            for (int indexNodeItem = START_NUMBER_FOR_LOOP; indexNodeItem < itemNodes.getLength(); indexNodeItem++) {
                checkCurrentNode(nodeQueue, roleList, itemNodes, indexNodeItem);
            }
        }
        return roleList;
    }

    private void checkCurrentNode(Queue<Node> nodeQueue, List<Item> roleList, NodeList itemNodes, int indexNodeItem) {
        Node currentNode = itemNodes.item(indexNodeItem);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap currentNodeAttributes = currentNode.getAttributes();
            if (currentNode.getNodeName().equals(TAG_NAME)) {
                if (currentNodeAttributes.getNamedItem(ATTRIBUTE_ITEM_ID) != null
                        && currentNodeAttributes.getNamedItem(ATTRIBUTE_ITEM_COLOR) != null) {
                    Item item = new Item(Integer.parseInt(currentNodeAttributes.getNamedItem(ATTRIBUTE_ITEM_ID).getNodeValue()),
                            currentNodeAttributes.getNamedItem(ATTRIBUTE_ITEM_COLOR).getNodeValue());
                    roleList.add(item);
                    uniqueListOfItem.add(item);
                }
            }
            nodeQueue.offer(itemNodes.item(indexNodeItem));
        }
    }
}
