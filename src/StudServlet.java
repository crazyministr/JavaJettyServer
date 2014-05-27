//====================================================================
// Задача: HttpServlet должен сохранять список студентов. Студенты
// идентифицируются уникальным "именем" и, в качестве атрибута, имеют 
// номер группы. HttpServlet должен обеспечивать обработку следующих
// запросов:
// - GET /students
//   выдает сохраненный список студентов в виде таблицы; 
// - POST /students?name=<имя студента>&group=<номер группы>
//   заносит нового (ранее не существовавшего) студента в таблицу
//   с указанным номером группы;
// - PUT /students/<имя студента>?group=<номер группы>
//   изменяет номер группы у существующего студента с заданным именем;
// - DELETE /students/<имя студента>
//   удаляет из списка студента с заданным именем.
// Таблица существует только на время жизни сервера (хранится в его
// оперативной памяти), при рестарте сервера таблица обнуляется. Если
// список студентов пуст, то запрос GET выдает сообщение об этом.
//====================================================================

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Реализация описанного сервлета. Адрес "/students" считается базовым для него. 
 */
public class StudServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String CONTENT_TYPE = "text/html; charset=utf-8";
    private static final String EMPTY_LIST =
            "<title>Список студентов</title>\n" +
                    "<h2>Список студентов пуст</h2>\n";
    private static final String STUD_TABLE =
            "<title>Список студентов</title>\n" +
                    "<h2>Список студентов</h2>\n" +
                    "<table style=\'width: 50%%; border: 1px solid black; border-collapse: collapse\'>\n" +
                    "  <col style=\'width: 60%%; border: 1px solid black\'/><col/>\n" +
                    "  %s" +
                    "</table>\n";
    private static final String STUD_ROW =
            "<tr><td>%s</td><td>%s</td></tr>\n";

    //============ Errors ===============

    private static final String ERROR = "<h2>ERROR %s</h2>\n<p>%s</p>\n";
    private static final String ERR_EMPTY_RESOURCE = "Не задано имя студента";
    private static final String ERR_NOT_EMPTY_RESOURCE = "Ресурс %s не найден";
    private static final String ERR_NAME_PAR_MISSED = "\'name\' параметр не задан";
    private static final String ERR_GROUP_PAR_MISSED = "\'group\' параметр не задан";
    private static final String ERR_STUDENT_EXISTS = "Студент %s уже существует; задайте уникальное имя";
    private static final String ERR_STUDENT_NOT_FOUND = "Студент %s не существует";

    public enum Error {
        OK(HttpServletResponse.SC_OK, "OK"),
        BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, "Bad request"),
        NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "Not found");

        private int code;
        private String message;

        private Error(int code, String s) {
            this.code = code;
            message = s;
        }

        @Override
        public String toString() { return String.format("%d: %s", code, message); }

        public int getCode() { return code; }
    }

    Map<String, String> students = new TreeMap<String, String>();

    @Override
    /**
     * Обработка запроса GET - выдача списка студентов
     * Формат запроса:
     * GET /students
     */
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("GET");
        response.setContentType(CONTENT_TYPE);
        String path = request.getPathInfo();
        try {
            if (path != null && !"/".equals(path)) {
                // Должен быть указан пустой ресурс
                error(response, Error.NOT_FOUND, String.format(ERR_NOT_EMPTY_RESOURCE, path));
            } else {
                // Формируем список
                response.getWriter().print(students.isEmpty() ? EMPTY_LIST : studList());
            }
        } catch (IOException e) {
        }
    }

    @Override
    /**
     * Обработка запроса POST - занесение в таблицу нового студента
     * Формат запроса:
     * POST /students?name=<имя студента>&group=<номер группы>
     */
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("POST");
        String path = request.getPathInfo();
        String name = request.getParameter("name");		// Имя студента
        String group = request.getParameter("group");	// Номер группы
        response.setContentType(CONTENT_TYPE);
        try {
            if (path != null && !"/".equals(path)) {
                // Должен быть указан пустой ресурс
                error(response, Error.NOT_FOUND, String.format(ERR_NOT_EMPTY_RESOURCE, path));
            } else if (name == null) {
                // Параметр 'name' не найден
                error(response, Error.BAD_REQUEST, ERR_NAME_PAR_MISSED);
            } else if (group == null) {
                // Параметр 'group' не найден
                error(response, Error.BAD_REQUEST, ERR_GROUP_PAR_MISSED);
            } else if (students.get(name) != null) {
                // Такой студент уже существует - не добавляем
                error(response, Error.BAD_REQUEST, String.format(ERR_STUDENT_EXISTS, name));
            } else {
                // Ошибок в запросе нет, добавляем студента
                students.put(name, group);
            }
        } catch (IOException e) {
        }
    }

    @Override
    /**
     * Обработка запроса PUT - изменение группы у существующего студента
     * Формат запроса:
     * PUT /students/<имя студента>?group=<номер группы>
     */
    protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("PUT");
        String path = request.getPathInfo();
        String group = request.getParameter("group");	// Номер группы
        response.setContentType(CONTENT_TYPE);
        try {
            if (path == null) {
                // Имя студента (ресурс) не задано
                error(response, Error.NOT_FOUND, ERR_EMPTY_RESOURCE);
            } else if (group == null) {
                // Номер группы не задан
                error(response, Error.BAD_REQUEST, ERR_GROUP_PAR_MISSED);
            } else {
                String name = path.substring(1);	// Имя студента
                if (students.get(name) == null) {
                    // Студент с заданным именем не найден
                    error(response, Error.NOT_FOUND, String.format(ERR_STUDENT_NOT_FOUND, name));
                } else {
                    // Все нормально, записываем информацию о группе
                    students.put(name, group);
                }
            }
        } catch (IOException e) {
        }
    }

    @Override
    /**
     * Обработка запроса DELETE - удаление студента с заданным именем.
     * Формат запроса:
     * DELETE /students/<имя студента>
     */
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response) {
        System.out.println("DELETE");
        String path = request.getPathInfo();
        response.setContentType(CONTENT_TYPE);
        try {
            if (path == null) {
                // Имя студента (ресурс) не задано
                error(response, Error.NOT_FOUND, ERR_EMPTY_RESOURCE);
            } else {
                String name = path.substring(1);
                if (students.get(name) == null) {
                    // Студент с заданным именем не найден
                    error(response, Error.NOT_FOUND, String.format(ERR_STUDENT_NOT_FOUND, name));
                } else {
                    // Все нормально, удаляем студента
                    students.remove(name);
                }
            }
        } catch (IOException e) {
        }
    }

    private String studList() {
        System.out.println("studList");
        StringBuilder rows = new StringBuilder();
        for (Map.Entry<String, String> entry : students.entrySet()) {
            rows.append(String.format(STUD_ROW, entry.getKey(), entry.getValue()));
        }
        return String.format(STUD_TABLE, rows.toString());
    }

    private void error(HttpServletResponse response, Error error, String reason) throws IOException {
        response.setStatus(error.getCode());
        response.getWriter().format(ERROR, error.toString(), reason);
    }
}
