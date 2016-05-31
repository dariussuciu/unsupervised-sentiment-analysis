package model;

public class Pair<T, TT> {

    public T first;
    public TT second;

    public Pair() {
    }

    public Pair(T first, TT second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return this.first;
    }

    public TT second() {
        return this.second;
    }

    public void setFirst(T o) {
        this.first = o;
    }

    public void setSecond(TT o) {
        this.second = o;
    }

    public String toString() {
        return "(" + this.first + "," + this.second + ")";
    }
}
