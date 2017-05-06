package ova;

public class Query<Result> {

    Class<Result> type;
    int start;
    int size;
    String oql;

    public int getStart() {
        return start;
    }

    public int getSize() {
        return size;
    }

    public String getOql() {
        return oql;
    }

    public Class<Result> getType() {
        return type;
    }

    private Query(Class<Result> type) {
        this.type = type;
    }

    public static <Result, PK> Query<Result> byPrimaryKey(Class<Result> type, PK pk) {

        Query<Result> q = new Query<Result>(type);
        q.size = -1;
        q.start = -1;
        q.oql = "@PK = " + pk;

        return q;
    }

    public static <Result> Query<Result> byType(Class<Result> type) {

        Query<Result> q = new Query<Result>(type);
        q.size = -1;
        q.start = -1;
        q.oql = "";

        return q;
    }

    public static <Result> Query<Result> byType(Class<Result> type, int size, int page) {

        Query<Result> q = new Query<Result>(type);
        q.size = size;
        q.start = page;
        q.oql = "";

        return q;
    }

}
