package xml.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xml.model.Item;
import java.sql.*;
import java.util.*;

public class ItemDao {
    private static final ItemDao INSTANCE = new ItemDao();
    private static final PoolDaoConnection POOL_DAO_CONNECTION = PoolDaoConnection.getInstance();
    private static final Logger LOG = LogManager.getLogger(ItemDao.class.getName());
    private static final String MESSAGE_ROLLBACK = "Откат транзакции на начало";
    private static final String MESSAGE_FINALLY_BLOCK = "С закрытием ресурсов пошло что то не так";

    private ItemDao() {

    }

    public static ItemDao getInstance() {
        return INSTANCE;
    }

    public List<Integer> getListIdByIdAndColor(int id, String color) {
        List<Integer> allId = new ArrayList<>();
        try (Connection connection = POOL_DAO_CONNECTION.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT i.id FROM item i INNER JOIN"
                     + " box_item bi ON i.id = bi.item_id WHERE bi.box_id=? AND i.color=?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, color);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                allId.add(fillListId(resultSet));
            }
        } catch (SQLException e) {
            LOG.error("Смотри в получение всех id из таблицы item по id и color", e);
        }
        return allId;
    }

    public List<Integer> getListItemId() {
        List<Integer> allId = new ArrayList<>();
        try (Connection connection = POOL_DAO_CONNECTION.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT id FROM box")) {
                while (resultSet.next()) {
                    allId.add(fillListId(resultSet));
                }
            }
        } catch (SQLException e) {
            LOG.error("Смотри в получение всех id из таблицы box", e);
        }
        return allId;
    }

    public Set<String> getListColor() {
        Set<String> allColor = new HashSet<>();
        try (Connection connection = POOL_DAO_CONNECTION.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT color FROM item")) {
                while (resultSet.next()) {
                    allColor.add(fillListColor(resultSet));
                }
            }
        } catch (SQLException e) {
            LOG.error("Смотри в получение списка цвутов из таблицы item", e);
        }
        return allColor;
    }

    private String fillListColor(ResultSet resultSet) throws SQLException {
        String color = resultSet.getString("color");
        return color;
    }

    private int fillListId(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        return id;
    }

    public void setInitStateOfDataBase(Map<Integer, List<Item>> initStateData, Set<Item> uniqueListOfItems) {
        Connection connection = null;
        try {
            connection = POOL_DAO_CONNECTION.getConnection();
            connection.setAutoCommit(false);
            insertDataToBox(initStateData);
            insertItems(uniqueListOfItems);
            insertDataToBoxItem(initStateData);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                LOG.error(MESSAGE_ROLLBACK, rollbackException);
            }
            LOG.error("Смотри в инициализацию базы данных", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqlException) {
                LOG.error(MESSAGE_FINALLY_BLOCK, sqlException);
            }
        }
    }

    private void insertDataToBox(Map<Integer, List<Item>> initStateData) throws SQLException {
        try (Connection connection = POOL_DAO_CONNECTION.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO box (id) VALUES "
                     + generateQueryForInsertToBox(initStateData.size()))) {
            setDataForInsertToBox(preparedStatement, initStateData);
            preparedStatement.executeUpdate();
        }
    }

    private void insertDataToBoxItem(Map<Integer, List<Item>> initStateOfDataBase) throws SQLException {
        for (Map.Entry<Integer, List<Item>> item : initStateOfDataBase.entrySet()) {
            List<Item> listItems = item.getValue();
            try (Connection connection = POOL_DAO_CONNECTION.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO box_item (box_id, item_id)  VALUES "
                         + generateQueryForInsertToBoxItem(listItems.size()))) {
                setDataForInsertToBoxItem(preparedStatement, listItems, item.getKey());
                preparedStatement.executeUpdate();
            }
        }
    }

    private String generateQueryForInsertToBoxItem(int countBlock) {
        return getValuesBlock(countBlock);
    }

    private String getValuesBlock(int countBlock) {
        StringBuilder blockValuesBoxItem = new StringBuilder();
        for (int index = 0; index < countBlock; index++) {
            blockValuesBoxItem.append("(");
            if (index != countBlock - 1) {
                blockValuesBoxItem.append("?, ?), ");
            } else {
                blockValuesBoxItem.append("?, ?);");
            }
        }
        return blockValuesBoxItem.toString();
    }

    private void setDataForInsertToBoxItem(PreparedStatement preparedStatement, List<Item> items, int parentId) throws SQLException {
        Iterator<Item> iterator = items.iterator();
        int startIndex = 1;
        while (iterator.hasNext()) {
            Item item = iterator.next();
            preparedStatement.setInt(startIndex++, parentId);
            preparedStatement.setInt(startIndex++, item.getId());
        }
    }

    private void insertItems(Set<Item> uniqueListOfItems) throws SQLException {
        int countBlock = uniqueListOfItems.size();
        try (Connection connection = POOL_DAO_CONNECTION.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO item (id, color) VALUES "
                     + generateQueryForInsertToItem(countBlock))) {
            setDataForInsertToItem(preparedStatement, uniqueListOfItems);
            preparedStatement.executeUpdate();
        }
    }

    private void setDataForInsertToItem(PreparedStatement preparedStatement, Set<Item> uniqueListOfItems) throws SQLException {
        Iterator<Item> iterator = uniqueListOfItems.iterator();
        int startIndex = 1;
        while (iterator.hasNext()) {
            Item item = iterator.next();
            preparedStatement.setInt(startIndex++, item.getId());
            preparedStatement.setString(startIndex++, item.getColor());
        }
    }

    private String generateQueryForInsertToItem(int countBlock) {
        return getValuesBlock(countBlock);
    }

    private void setDataForInsertToBox(PreparedStatement preparedStatement, Map<Integer, List<Item>> initStateOfDataBase) throws SQLException {
        Set<Integer> boxId = initStateOfDataBase.keySet();
        Iterator<Integer> iterator = boxId.iterator();
        int startIndex = 1;
        while (iterator.hasNext()) {
            preparedStatement.setInt(startIndex++, iterator.next());
        }
    }

    private String generateQueryForInsertToBox(int countQuestionMark) {
        StringBuilder blockIn = new StringBuilder();
        for (int index = 0; index < countQuestionMark; index++) {
            blockIn.append("(");
            if (index != countQuestionMark - 1) {
                blockIn.append("?), ");
            } else {
                blockIn.append("?);");
            }
        }
        return blockIn.toString();
    }
}
