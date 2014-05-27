public class Square {
    private String x;
    private String y;
    private String size;
    private String color;
    private String number;

    public Square(String x, String y, String size, String color, String number) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        this.number = number;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getNumber() {
        return number;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String toString() {
        return "<div style=\"position: absolute; " + "width: " + size + "px;" + "height: " + size + "px;" +
                "background-color: " + color + ";" + "left: " + x + "px;" + "top: " + y + "px; \">" + number + "</div>\n";
    }
}
