import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SquaresServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=utf-8";
    private int countSquares = 0;
    private Map<String, Square> squares = new HashMap<String, Square>();
    String[] parameters = new String[] {"x", "y", "size", "color"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        String id = "";
        try {
            id = request.getPathInfo().split("/")[1];
        } catch (Exception e) {
            if (squares.isEmpty()) {
                response.getWriter().println("Base empty.");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                String html = "﻿<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">" +
                        "</head>\n" +
                        "<body>" +
                        "   <div id=\"square\" style=\"height: 100%; width:100%;\"></div>";
                for (Square square : squares.values()) {
                    html += square.toString();
                }
                html += "</div></body></html>";
                response.getWriter().println(html);
                response.setStatus(HttpServletResponse.SC_OK);
            }
            return;
        }
        if (!squares.containsKey(id)) {
            response.getWriter().println("Square not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            Square square = squares.get(id);
            response.getWriter().println("Square " + square.getNumber() + ":\n" +
                    "x = " + square.getX() + ";\n" +
                    "y = " + square.getY() + ";\n" +
                    "size = " + square.getSize() + ";\n" +
                    "color = " + square.getColor() + ";\n");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        for (String param: parameters) {
            if (param.equals("color")) {
                continue;
            }
            try {
                Integer.parseInt(requestParameterMap.get(param)[0]);
            } catch (Exception e) {
                response.getWriter().println("parameter '" + param + "' wrong or not found");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        try {
            String t = requestParameterMap.get("color")[0];
        } catch (Exception e) {
            response.getWriter().println("parameter 'color' wrong or not found");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        countSquares += 1;
        squares.put(String.valueOf(countSquares), new Square(
                requestParameterMap.get("x")[0],
                requestParameterMap.get("y")[0],
                requestParameterMap.get("size")[0],
                requestParameterMap.get("color")[0],
                String.valueOf(countSquares)));
        response.getWriter().println("square " + countSquares + " created.");
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        String id = "";
        try {
            id = request.getPathInfo().split("/")[1];
        } catch (Exception e) {
            response.getWriter().println("Not found id square");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!squares.containsKey(id)) {
            response.getWriter().println("Square not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        for (String param : parameters) {
            if (param.equals("color")) {
                if (requestParameterMap.containsKey(param)) {
                    squares.get(id).setColor(requestParameterMap.get("color")[0]);
                    continue;
                } else {
                    response.getWriter().println("parameter 'color' wrong or not found");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
            if (requestParameterMap.containsKey(param)) {
                try {
                    Integer.parseInt(requestParameterMap.get(param)[0]);
                } catch (Exception e) {
                    response.getWriter().println("parameter '" + param + "' wrong or not found");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
        }
        squares.get(id).setX(requestParameterMap.get("x")[0]);
        squares.get(id).setY(requestParameterMap.get("y")[0]);
        squares.get(id).setSize(requestParameterMap.get("size")[0]);
        response.getWriter().println("square " + id + " changed.");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        String id = "";
        try {
            id = request.getPathInfo().split("/")[1];
        } catch (Exception e) {
            response.getWriter().println("Not found id square");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!squares.containsKey(id)) {
            response.getWriter().println("Square not found");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        squares.remove(id);
        response.getWriter().println("Success delete " + id + " square.");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
