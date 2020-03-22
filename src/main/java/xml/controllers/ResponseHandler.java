package xml.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResponseHandler {
    public ResponseHandler() {

    }

    public String buildMap(List<Integer> listForBuildInt, Set<String> listForBuildString) throws IOException {
        return new ObjectMapper().writeValueAsString(Map.of("id", listForBuildInt,
                "colors", listForBuildString));
    }

    public String buildList(List<Integer> listForBuild) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(listForBuild);
    }

    public void sendResponse(String json, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");
        out.write(json);
        out.flush();
    }
}
