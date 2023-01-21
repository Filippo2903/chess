package client;


public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.initGame();
    }
}

//import java.awt.*;
//import java.util.function.Function;
//
//public class Main {
//    private Function<Point, Boolean> function;
//
//    public Main(Function<Point, Boolean> function) {
//        this.function = function;
//    }
//
//    public boolean executeFunction(Point pt) {
//        return function.apply(pt);
//    }
//
//    public static void main(String[] args) {
//        Function<Point, Boolean> test = pt -> {
//            int a = 5;
//            pt.x += a;
//            return pt.x == pt.y;
//        };
//        Main a = new Main(test);
//        System.out.println(a.executeFunction(new Point(1, 6)));
//    }
//}
