package xml.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xml.persistence.ItemDao;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ItemService {
    private static final Logger LOG = LogManager.getLogger(ItemService.class.getName());
    private static final ItemDao ITEM_DAO = ItemDao.getInstance();
    private final static ItemService INSTANCE = new ItemService();

    private ItemService() {

    }

    public static ItemService getInstance() {
        return INSTANCE;
    }

    public void InitStateOfDataBase(String filePath) {
        HandlerXml handlerXml = new HandlerXml();
        try {
            ITEM_DAO.setInitStateOfDataBase(handlerXml.parseXml(filePath), handlerXml.getUniqueListOfItem());
        } catch (IOException io) {
            LOG.error("Не верно задан путь", io);
        }
    }

    public List<Integer> getListItemId() {
        return ITEM_DAO.getListItemId();
    }

    public Set<String> getListColor() {
        return ITEM_DAO.getListColor();
    }

    public List<Integer> getListIdByIdAndColor(int id, String color) {
        return ITEM_DAO.getListIdByIdAndColor(id, color);
    }
}
