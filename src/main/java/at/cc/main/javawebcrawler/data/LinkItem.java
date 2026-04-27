package at.cc.main.javawebcrawler.data;

public record LinkItem(String link, boolean isBroken) {

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof LinkItem otherObj)) return false;
        return link.equals(otherObj.link);
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }
}
