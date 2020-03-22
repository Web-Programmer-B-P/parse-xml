package xml.controllers;

import xml.services.ItemService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/get-found-data")
public class ResponseController extends HttpServlet {
    private final static ItemService ITEM_SERVICE = ItemService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        String color = req.getParameter("color");
        if (id != null && color != null) {
            ResponseHandler responseHandler = new ResponseHandler();
            String json = responseHandler.buildList(ITEM_SERVICE.getListIdByIdAndColor(Integer.parseInt(id), color));
            responseHandler.sendResponse(json, resp);
        }
    }
}
