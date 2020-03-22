package xml.controllers;

import xml.services.ItemService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/get-data")
public class InitController extends HttpServlet {
    private final static ItemService ITEM_SERVICE = ItemService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String filePath = req.getParameter("path");
        if (filePath != null) {
            ITEM_SERVICE.InitStateOfDataBase(filePath);
            ResponseHandler responseHandler = new ResponseHandler();
            String json = responseHandler.buildMap(ITEM_SERVICE.getListItemId(), ITEM_SERVICE.getListColor());
            responseHandler.sendResponse(json, resp);
        }
    }
}
