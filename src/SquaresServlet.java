import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SquaresServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=utf-8";
    private int numbers = 0;
    private List<Square> squares = new LinkedList<Square>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request);
        response.setContentType(CONTENT_TYPE);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request);
        response.setContentType(CONTENT_TYPE);
        String path = request.getPathInfo();
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String size = request.getParameter("size");
        String color = request.getParameter("color");
        try {
            squares.add(new Square(x, y, size, color, String.valueOf(numbers)));
            numbers += 1;
        } catch (Exception e) {
            System.out.println("Failed POST");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getPathInfo());
        response.setContentType(CONTENT_TYPE);
        String x = request.getParameter("x");
        String y = request.getParameter("y");
        String size = request.getParameter("size");
        String color = request.getParameter("color");
        String numberSquare = request.getPathInfo().substring(1);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request);
        response.setContentType(CONTENT_TYPE);
    }
}
