package at.cc.main.javawebcrawler.data.webpage;

public record Link(String link, boolean isBroken) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Link otherObj)) return false;
        return link.equals(otherObj.link);
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }
}
